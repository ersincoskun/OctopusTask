package com.octopus.task.model

data class ResponseModel(
    val params: List<ParamsItem>?
)

data class ParamsItem(
    val report: Report?,
    val sync: Sync?
)

data class Report(
    val command_id: Int?
)

data class Sync(
    val command_id: Int?,
    val data: List<DataItem>?
)

data class DataItem(
    val nth: Int?,
    val type: String?,
    val name: String?,
    val start_date: Any?,
    val end_date: Any?
)

data class SpecifyBodyModel(
    val command_id: String
)

data class SpecifyResponseModel(
    val success: Boolean?,
    val message: Message?
)

data class Message(val status: String?)