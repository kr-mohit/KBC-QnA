package com.pramoh.kbcqna.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.model.ApiDataDTO
import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.domain.model.AppUpdateInfo
import com.pramoh.kbcqna.domain.model.LeaderboardPlayerWithId
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.model.toEntity
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import kotlinx.coroutines.tasks.await

class MainRepositoryImpl(
    private val context: Context,
    private val leaderboardDB: LeaderboardDB
) : MainRepository {

    override suspend fun getQuestionsFromLocal(): Response<List<Question>> {
        return try {
            val jsonString = context.resources.openRawResource(R.raw.questions).bufferedReader()
                .use { it.readText() }
            val apiDataDTO = Gson().fromJson(jsonString, ApiDataDTO::class.java)
            val allQuestions = apiDataDTO.data.map { it.toDomainQuestion() }

            val prizeAmounts = listOf(
                1000, 5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000,
                1250000, 2500000, 5000000, 10000000, 30000000, 100000000
            )

            val selectedQuestions = prizeAmounts.mapNotNull { prize ->
                allQuestions.filter { it.prizeAmount == prize }.randomOrNull()
            }

            if (selectedQuestions.size == 15) {
                Response.Success(selectedQuestions)
            } else {
                Response.Error("Insufficient local questions parsed. Expected 15, got ${selectedQuestions.size}")
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to parse local questions")
        }
    }

    override suspend fun getQuestionsFromRemote(
        url: String,
        questionCount: Int?
    ): Response<List<Question>> {
        return try {

            val db = FirebaseFirestore.getInstance()

            val configSnapshot = db.collection("config")
                .document("game_config")
                .get()
                .await()

            if (configSnapshot.exists()) {
                MoneyTypeConversionUtil.prefix = configSnapshot.getString("currencyPrefix") ?: ""
                MoneyTypeConversionUtil.suffix = configSnapshot.getString("currencySuffix") ?: ""
            }

            var prizeAmounts = if (configSnapshot.exists()) {
                val list = configSnapshot.get("prizeAmounts") as? List<*>
                list?.mapNotNull { (it as? Number)?.toInt() }?.sorted()
            } else {
                null
            } ?: listOf(
                1000, 5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000,
                1250000, 2500000, 5000000, 10000000, 30000000, 100000000
            )

            // If a specific questionCount is requested by the client, slice the prize list to match that size
            if (questionCount != null && questionCount > 0 && questionCount <= prizeAmounts.size) {
                prizeAmounts = prizeAmounts.take(questionCount)
            }

            // 2. Fetch all questions
            val snapshot = db.collection("questions")
                .get()
                .await()

            val questionsWithIds = snapshot.documents.map { doc ->
                doc.id to Question(
                    question = doc.getString("question") ?: "",
                    option1 = doc.getString("option1") ?: "",
                    option2 = doc.getString("option2") ?: "",
                    option3 = doc.getString("option3") ?: "",
                    option4 = doc.getString("option4") ?: "",
                    correctOptionNumber = (doc.getLong("correctOptionNumber") ?: 1).toInt(),
                    prizeAmount = (doc.getLong("prizeAmount") ?: 0).toInt(),
                    region = doc.getString("region") ?: "GLOBAL"
                )
            }

            leaderboardDB.getQuestionDAO().deleteAllQuestions()
            leaderboardDB.getQuestionDAO()
                .insertAll(questionsWithIds.map { (id, question) -> question.toEntity(id) })

            val allQuestions = questionsWithIds.map { it.second }

            // 3. Select one random question per configured prize level
            val selectedQuestions = prizeAmounts.mapNotNull { prize ->
                allQuestions.filter { it.prizeAmount == prize }.randomOrNull()
            }

            if (selectedQuestions.size == prizeAmounts.size) {
                Response.Success(selectedQuestions)
            } else {
                Response.Error("Insufficient remote questions. Expected ${prizeAmounts.size} matching prize levels, but only got ${selectedQuestions.size}")
            }

        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Firestore sync failed")
        }
    }

    override suspend fun getTopPlayers(): Response<List<PlayerData>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("leaderboard")
                .orderBy("moneyWon", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            val players = snapshot.documents.mapIndexed { index, doc ->
                PlayerData(
                    id = index,
                    playerName = doc.getString("playerName") ?: "",
                    moneyWon = (doc.getLong("moneyWon") ?: 0).toInt()
                )
            }
            Response.Success(players)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to fetch leaderboard from Firebase")
        }
    }

    override suspend fun insertPlayer(player: PlayerData) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("leaderboard")
                .orderBy("moneyWon", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val playersWithDocs = snapshot.documents.map { doc ->
                doc.id to PlayerData(
                    id = 0,
                    playerName = doc.getString("playerName") ?: "",
                    moneyWon = (doc.getLong("moneyWon") ?: 0).toInt()
                )
            }

            if (playersWithDocs.size < 20) {
                val newDoc = mapOf(
                    "playerName" to player.playerName,
                    "moneyWon" to player.moneyWon
                )
                db.collection("leaderboard").add(newDoc).await()
            } else {
                val lowestPlayerPair = playersWithDocs.minByOrNull { it.second.moneyWon }
                if (lowestPlayerPair != null && player.moneyWon > lowestPlayerPair.second.moneyWon) {
                    val newDoc = mapOf(
                        "playerName" to player.playerName,
                        "moneyWon" to player.moneyWon
                    )
                    db.collection("leaderboard").add(newDoc).await()
                    db.collection("leaderboard").document(lowestPlayerPair.first).delete().await()
                }
            }
        } catch (e: Exception) {
            // handle error
        }
    }

    override suspend fun deleteAllPlayers() {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("leaderboard").get().await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            // handle error
        }
    }

    override suspend fun checkPlayerNameExists(name: String): Response<Boolean> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("leaderboard")
                .whereEqualTo("playerName", name)
                .get()
                .await()
            Response.Success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to check if player name exists")
        }
    }

    override suspend fun checkForAppUpdate(): Response<AppUpdateInfo> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("config")
                .document("app_update")
                .get()
                .await()

            val gameConfigSnapshot = db.collection("config")
                .document("game_config")
                .get()
                .await()

            val prefixVal =
                if (gameConfigSnapshot.exists()) gameConfigSnapshot.getString("currencyPrefix")
                    ?: "" else ""
            val suffixVal =
                if (gameConfigSnapshot.exists()) gameConfigSnapshot.getString("currencySuffix")
                    ?: "" else ""

            if (snapshot.exists()) {
                val newVersion = snapshot.getString("newVersion")
                    ?: snapshot.getString("versionName")
                    ?: "1.0"
                val dialogType = snapshot.getString("dialogType")
                    ?: if (snapshot.getBoolean("forceUpdate") == true) "hard" else "soft"
                val updateMessage = snapshot.getString("updateMessage")
                    ?: snapshot.getString("message")
                    ?: ""
                val isMaintenance = snapshot.getBoolean("isMaintenanceMode") ?: false
                val maintenanceMsg = snapshot.getString("maintenanceMessage") ?: ""
                val adminPasskey = snapshot.getString("adminPasskey")
                val apkUrl = snapshot.getString("apkUrl")

                Response.Success(
                    AppUpdateInfo(
                        newVersion = newVersion,
                        dialogType = dialogType,
                        updateMessage = updateMessage,
                        isMaintenanceMode = isMaintenance,
                        maintenanceMessage = maintenanceMsg,
                        adminPasskey = adminPasskey,
                        currencyPrefix = prefixVal,
                        currencySuffix = suffixVal,
                        apkUrl = apkUrl
                    )
                )
            } else {
                Response.Success(
                    AppUpdateInfo(
                        newVersion = "1.0",
                        dialogType = "none",
                        updateMessage = "",
                        isMaintenanceMode = false,
                        maintenanceMessage = "",
                        adminPasskey = null,
                        currencyPrefix = prefixVal,
                        currencySuffix = suffixVal
                    )
                )
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to check for app update")
        }
    }

    override suspend fun getUniquePrizeAmounts(): List<Int> {
        // Try getting from Room DB first
        val dbQuestions = leaderboardDB.getQuestionDAO().getAllQuestions()
        if (dbQuestions.isNotEmpty()) {
            return dbQuestions.map { it.prizeAmount }.distinct().sorted()
        }
        // Fallback to raw resource questions
        return try {
            val jsonString = context.resources.openRawResource(R.raw.questions).bufferedReader()
                .use { it.readText() }
            val apiDataDTO = Gson().fromJson(jsonString, ApiDataDTO::class.java)
            apiDataDTO.data.map { it.toDomainQuestion() }.map { it.prizeAmount }.distinct().sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRemotePrizeAmounts(): Response<List<Int>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val configSnapshot = db.collection("config")
                .document("game_config")
                .get()
                .await()

            val prizeAmounts = if (configSnapshot.exists()) {
                val list = configSnapshot.get("prizeAmounts") as? List<*>
                list?.mapNotNull { (it as? Number)?.toInt() }
            } else {
                null
            } ?: listOf(
                1000, 5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000,
                1250000, 2500000, 5000000, 10000000, 30000000, 100000000
            )
            Response.Success(prizeAmounts)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to fetch remote config")
        }
    }

    override suspend fun updateRemotePrizeAmounts(prizeAmounts: List<Int>): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("config")
                .document("game_config")
                .update("prizeAmounts", prizeAmounts)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to update prize amounts in Firebase")
        }
    }

    override suspend fun updateRemoteAppUpdateInfo(
        newVersion: String,
        dialogType: String,
        updateMessage: String
    ): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "newVersion" to newVersion,
                "dialogType" to dialogType,
                "updateMessage" to updateMessage
            )
            db.collection("config")
                .document("app_update")
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to update app update settings in Firebase")
        }
    }

    override suspend fun getRemoteLeaderboardWithDocIds(): Response<List<LeaderboardPlayerWithId>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("leaderboard")
                .orderBy("moneyWon", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val players = snapshot.documents.map { doc ->
                LeaderboardPlayerWithId(
                    docId = doc.id,
                    playerName = doc.getString("playerName") ?: "",
                    moneyWon = (doc.getLong("moneyWon") ?: 0).toInt()
                )
            }
            Response.Success(players)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to fetch leaderboard from Firebase")
        }
    }

    override suspend fun deleteRemoteLeaderboardPlayers(docIds: List<String>): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            for (id in docIds) {
                val docRef = db.collection("leaderboard").document(id)
                batch.delete(docRef)
            }
            batch.commit().await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to delete players from Firebase")
        }
    }

    override suspend fun getRemoteQuestionStats(): Response<Map<Int, Int>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("questions").get().await()
            val countsMap = snapshot.documents.mapNotNull { doc ->
                doc.getLong("prizeAmount")?.toInt()
            }.groupingBy { it }.eachCount()
            Response.Success(countsMap)
        } catch (e: Exception) {
            Response.Error(
                e.localizedMessage ?: "Failed to fetch question statistics from Firebase"
            )
        }
    }

    override suspend fun updateRemoteMaintenanceInfo(
        isMaintenance: Boolean,
        message: String
    ): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "isMaintenanceMode" to isMaintenance,
                "maintenanceMessage" to message
            )
            db.collection("config")
                .document("app_update")
                .update(data)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(
                e.localizedMessage ?: "Failed to update maintenance settings in Firebase"
            )
        }
    }

    override suspend fun addRemoteQuestion(question: Question): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "question" to question.question,
                "option1" to question.option1,
                "option2" to question.option2,
                "option3" to question.option3,
                "option4" to question.option4,
                "correctOptionNumber" to question.correctOptionNumber,
                "prizeAmount" to question.prizeAmount,
                "region" to question.region
            )
            db.collection("questions")
                .add(data)
                .await()
            Response.Success(Unit)
        } catch (e: java.lang.Exception) {
            Response.Error(e.localizedMessage ?: "Failed to add question to Firebase")
        }
    }

    override suspend fun submitFeedback(
        name: String,
        email: String,
        type: String,
        message: String
    ): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "name" to name,
                "email" to email,
                "type" to type,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("feedback")
                .add(data)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to submit feedback")
        }
    }

    override suspend fun getRemoteFeedbacks(): Response<List<com.pramoh.kbcqna.presentation.admin.AdminFeedbackOption>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("feedback")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val feedbacks = snapshot.documents.map { doc ->
                com.pramoh.kbcqna.presentation.admin.AdminFeedbackOption(
                    docId = doc.id,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    type = doc.getString("type") ?: "",
                    message = doc.getString("message") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Response.Success(feedbacks)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to fetch feedbacks")
        }
    }

    override suspend fun deleteRemoteFeedbacks(docIds: List<String>): Response<Unit> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            for (id in docIds) {
                val docRef = db.collection("feedback").document(id)
                batch.delete(docRef)
            }
            batch.commit().await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to delete feedbacks")
        }
    }
}