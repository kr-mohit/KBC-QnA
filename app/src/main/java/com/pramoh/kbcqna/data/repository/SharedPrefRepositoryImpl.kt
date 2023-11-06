package com.pramoh.kbcqna.data.repository

import android.content.Context
import com.pramoh.kbcqna.data.model.WonLostDataDTO
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import com.pramoh.kbcqna.utils.Constants

class SharedPrefRepositoryImpl(val context: Context): SharedPrefRepository {

    private val pref = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    override fun setWonLostData(value: WonLostData) {
        Constants.PREF_WON_DATA.put(value.wins)
        Constants.PREF_LOST_DATA.put(value.loses)
    }

    override fun getWonLostData(): WonLostDataDTO {
        return WonLostDataDTO(
            Constants.PREF_WON_DATA.getInt(),
            Constants.PREF_LOST_DATA.getInt()
        )
    }

    override fun setMusicOnOff(value: Boolean) {
        Constants.PREF_MUSIC_ON_OFF.put(value)
    }

    override fun getMusicOnOff(): Boolean {
        return Constants.PREF_MUSIC_ON_OFF.getBoolean()
    }

    override fun setSfxAudioOnOff(value: Boolean) {
        Constants.PREF_SFX_AUDIO_ON_OFF.put(value)
    }

    override fun getSfxAudioOnOff(): Boolean {
        return Constants.PREF_SFX_AUDIO_ON_OFF.getBoolean()
    }

    override fun setSelectedRegion(value: String) {
        Constants.PREF_REGION_SELECTED.put(value)
    }

    override fun getSelectedRegion(): String {
        return Constants.PREF_REGION_SELECTED.getString()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getBoolean() = pref.getBoolean(this, false)

}