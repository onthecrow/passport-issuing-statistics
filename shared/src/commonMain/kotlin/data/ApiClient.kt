package data

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.random.Random

class ApiClient {

    private var client = HttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getPassportDetails(uid: String): Passport {
        var response = client.get("https://info.midpass.ru/api/request/$uid")
        if (response.status.value == 403) {
            client = HttpClient {
                install(UserAgent) {
                    agent = Random.nextInt(100000, 999999).toString().also { Napier.e("$$$$ new user agent: $it") }
                }
            }
            delay(10000)
            response = client.get("https://info.midpass.ru/api/request/$uid")
        }
        return json.decodeFromString(response.bodyAsText())
    }
}