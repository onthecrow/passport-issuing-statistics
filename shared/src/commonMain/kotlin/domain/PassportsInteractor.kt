package domain

import data.ApiClient
import data.Passport
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class PassportsInteractor {

    private val apiClient = ApiClient()
    private val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    private lateinit var localDate: LocalDate

    val passports: Channel<List<Passport>> = Channel()

    suspend fun getPassports(initialUid: String) {
        val date = initialUid.substringAfter("200065601")
            .substringBefore("0000")
        val id = initialUid.substringAfter("0000")
        val year = initialUid.substring(9, 13).toInt()
        val month = initialUid.substring(13, 15).toInt()
        val day = initialUid.substring(15, 17).toInt()
        localDate = LocalDate(year, month, day)
        var requestDate = localDate
        var requestId = id.toInt()
        var result = listOf<Passport>()
        withContext(Dispatchers.IO) {
            while (checkDate(requestDate)) {
                val details =
                    getPassportDetails("200065601${requestDate.toRequestDate()}0000$requestId")
                if (details == null) {
                    requestDate = requestDate.plus(1, DateTimeUnit.DAY)
                } else {
                    result = result + details
                    passports.send(result)
                    Napier.e("$$$$ Success id: 200065601${requestDate.toRequestDate()}0000$requestId")
                    requestId += 1
//                if (requestId == 3910) {
//                    requestId += 5
//                }
                }
            }
            Napier.e("$$$$ END")
        }
    }

    private suspend fun getPassportDetails(uid: String): Passport? {
        return try {
            Napier.e("$$$$ startRequest: $uid")
            apiClient.getPassportDetails(uid)
        } catch (error: Throwable) {
            Napier.e(error.message.toString(), error)
            Napier.e("$$$$ errorRequest: $uid")
            null
        }
    }

    private fun checkDate(date: LocalDate): Boolean {
        return date.toEpochDays() < currentDate.date.toEpochDays()
    }

    fun LocalDate.toRequestDate(): String {
        val day = if (dayOfMonth.toString().length == 1) {
            "0$dayOfMonth"
        } else {
            dayOfMonth.toString()
        }
        val month = if (monthNumber.toString().length == 1) {
            "0$monthNumber"
        } else {
            monthNumber.toString()
        }
        return "$year$month$day"
    }
}