package com.etaratasy.app.model

enum class TypeNotification { MAJORITE, PERMIS, RENDEZ_VOUS, MISE_A_JOUR }

data class Notification(
    val id: Long,
    val type: TypeNotification,
    val titre: String,
    val corps: String,
    val quand: String,
    val lue: Boolean = false
)
