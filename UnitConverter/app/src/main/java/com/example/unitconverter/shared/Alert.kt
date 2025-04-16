package com.example.unitconverter.shared

enum class AlertType {
    SUCCESS,
    WARNING,
    DANGER,
    INFO
}

fun handleAlert(
    message: String,
    type: AlertType = AlertType.INFO,
    showAlertCallback: (Boolean) -> Unit,
    alertMessageCallback: (String) -> Unit,
    alertTypeCallback: (AlertType) -> Unit
) {
    alertMessageCallback(message)
    alertTypeCallback(type)
    showAlertCallback(true)
}