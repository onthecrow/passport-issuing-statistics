package data

import kotlinx.serialization.Serializable

@Serializable
data class Passport(
    val uid: String,
    val sourceUid: String?,
    val receptionDate: String,
    val passportStatus: PassportStatus,
    val internalStatus: InternalStatus,
)

@Serializable
data class PassportStatus(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val subscription: Boolean = false,
)

@Serializable
data class InternalStatus(
    val name: String,
    val percent: Int,
)