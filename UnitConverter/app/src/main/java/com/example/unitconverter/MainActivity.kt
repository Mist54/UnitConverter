package com.example.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.unitconverter.shared.AlertType
import com.example.unitconverter.shared.handleAlert
import com.example.unitconverter.ui.theme.UnitConverterTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Unit converter") })},
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ){
                    paddingValues ->
                    UnitConverter(modifier = Modifier.padding(paddingValues),
                        snackbarHostState = snackbarHostState)
                }
            }
        }

    }



}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverter(modifier: Modifier,snackbarHostState: SnackbarHostState){
    val scope = rememberCoroutineScope()

    val categories  = listOf("Length", "Currency","Duration", "Angle")

    val unitMap = mapOf(
        "Length" to listOf("Meter", "Centimeter", "Feet", "Inch", "Kilometer", "Micrometer", "Millimeter", "Yard"),
        "Duration" to listOf("Seconds", "Minutes", "Hours", "Days", "Weeks", "Months", "Years")

    )

    var selectedCategory by remember { mutableStateOf(categories .first()) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    //unit of length - updated in future with different Units like duration and Angle.
    var currentUnit = unitMap[selectedCategory] ?: emptyList()

    //used for sub dropdowns
    var selectedSourceUnit by remember { mutableStateOf(currentUnit.first()) }
    var isSourceUnitMenuExpanded by remember { mutableStateOf(false) }

    var selectedTargetUnit by remember { mutableStateOf(currentUnit[1]); }
    var isTargetUnitMenuExpanded by remember { mutableStateOf(false) }

    // input and result section
    var inputValue by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    //alert section - Important
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var alertType by remember { mutableStateOf(AlertType.INFO) }



    //UI setup
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        //Alert UI section
        AnimatedVisibility(
            visible = showAlert,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier.zIndex(1f).align(Alignment.CenterHorizontally)
        ){
            val backgroundColor = when (alertType) {
                AlertType.SUCCESS -> Color(0xFF4CAF50) // Green
                AlertType.WARNING -> Color(0xFFFFC107) // Yellow
                AlertType.DANGER -> Color(0xFFF44336)  // Red
                AlertType.INFO -> Color(0xFF2196F3)    // Blue
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp)
                    .clickable { showAlert = false }
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    val iconRes = when (alertType) {
                        AlertType.SUCCESS -> Icons.Filled.CheckCircle
                        AlertType.WARNING -> Icons.Filled.Warning
                        AlertType.DANGER -> Icons.Filled.Close
                        AlertType.INFO -> Icons.Filled.Info
                    }
                    Icon(
                        imageVector = iconRes,
                        contentDescription = "Alert Icon",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alertMessage,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Alert",
                        tint = Color.White,
                        modifier = Modifier.clickable { showAlert = false }
                    )
                }
                // Auto-hide the alert after 3 seconds
                LaunchedEffect(showAlert) {
                    if (showAlert) {
                        delay(3000)
                        showAlert = false
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(0.dp,20.dp,0.dp,20.dp))
        ExposedDropdownMenuBox(
            expanded = isCategoryMenuExpanded,
            onExpandedChange = {isCategoryMenuExpanded = !isCategoryMenuExpanded}
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = {Text("Category")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = isCategoryMenuExpanded,
                onDismissRequest = {isCategoryMenuExpanded = false }
            ) {
                categories.forEach { category  ->
                    DropdownMenuItem(text = { Text(text = category )},
                        onClick = {
                            selectedCategory = category
                            currentUnit = unitMap[category] ?: emptyList()
                            selectedSourceUnit = currentUnit.firstOrNull() ?: ""
                            selectedTargetUnit = if (currentUnit.size > 1) currentUnit[1] else currentUnit.firstOrNull() ?: ""
                            isCategoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //from unit selector
            ExposedDropdownMenuBox(expanded = isSourceUnitMenuExpanded,
                onExpandedChange = { isSourceUnitMenuExpanded = !isSourceUnitMenuExpanded},
                modifier = Modifier.weight(1f)
            )
            {
                TextField(
                    value = selectedSourceUnit ,
                    onValueChange ={},
                    readOnly = true,
                    label = {Text("From unit")},
                    modifier = Modifier
                        .menuAnchor() //used to make the dropdown work
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSourceUnitMenuExpanded)

                    }
                )
                ExposedDropdownMenu(
                    expanded = isSourceUnitMenuExpanded,
                    onDismissRequest = {isSourceUnitMenuExpanded = false},

                ) {
                    currentUnit.forEach { lengthUnit ->
                        DropdownMenuItem(
                            text = { Text(text = lengthUnit)},
                            onClick = {
                                selectedSourceUnit = lengthUnit
                                isSourceUnitMenuExpanded = false
                            })

                    }

                }
            }

            //to unit selector
            ExposedDropdownMenuBox(expanded = isTargetUnitMenuExpanded,
                onExpandedChange = {isTargetUnitMenuExpanded = !isTargetUnitMenuExpanded},
                modifier = Modifier.weight(1f)
                )
            {
                TextField(
                    value = selectedTargetUnit,
                    onValueChange ={},
                    readOnly = true,
                    label = {Text("To unit")},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTargetUnitMenuExpanded)
                    })
                ExposedDropdownMenu(
                    expanded = isTargetUnitMenuExpanded,
                    onDismissRequest = { isTargetUnitMenuExpanded = false
                    })
                {
                    currentUnit.forEach { lengthUnit ->
                        DropdownMenuItem(
                            text = { Text(text = lengthUnit)},
                            onClick = {
                                selectedTargetUnit = lengthUnit
                                isTargetUnitMenuExpanded = false
                            })

                    }

                }

            }

        }
        Spacer(modifier = Modifier.padding(5.dp))
        //Input field
        OutlinedTextField(
            value = inputValue,
            onValueChange = {inputValue = it},
            label = {Text(text = "Enter the value")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Convert Button
            Button(
                onClick = {
                    if(inputValue.isBlank()){
                        // Show warning for empty input
                        handleAlert(
                            message = "Please enter a value to convert",
                            type = AlertType.WARNING,
                            showAlertCallback = { showAlert = it },
                            alertMessageCallback = { alertMessage = it },
                            alertTypeCallback = { alertType = it }
                        )

                    }else{
                        val conversionResult = convertUnit(
                            category = selectedCategory,
                            inputValue = inputValue,
                            sourceUnit = selectedSourceUnit,
                            targetUnit = selectedTargetUnit,
                            showAlert = { show ->
                                if (show) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = alertMessage,
                                            actionLabel = "Dismiss"
                                        )
                                    }
                                }
                            },
                            setAlertMessage = { alertMessage = it },
                            setAlertType = { alertType = it }
                        )
                        result = conversionResult

                        if(conversionResult != "Invalid" && conversionResult != "Unsupported category"){
                            handleAlert(
                                message = "Conversion completed successfully!",
                                type = AlertType.SUCCESS,
                                showAlertCallback = { showAlert = it },
                                alertMessageCallback = { alertMessage = it },
                                alertTypeCallback = { alertType = it }
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Convert")
            }

            // Clear Button
            Button(
                onClick = {
                    inputValue = ""
                    selectedCategory = categories.first()
                    selectedSourceUnit = currentUnit.firstOrNull() ?: ""
                    selectedTargetUnit = currentUnit.getOrNull(1) ?: ""
                    result = ""

                    handleAlert(
                        message = "All fields have been cleared",
                        type = AlertType.INFO,
                        showAlertCallback = { showAlert = it },
                        alertMessageCallback = { alertMessage = it },
                        alertTypeCallback = { alertType = it }
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Clear")
            }
        }


        Text(
            text = if (result.isNotBlank()) "Result: $result" else "Result: -",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

    }

}

/*backend functions for conversion*/
fun convertUnit(
    category: String,
    inputValue: String,
    sourceUnit: String,
    targetUnit: String,
    showAlert: (Boolean) -> Unit,
    setAlertMessage: (String) -> Unit,
    setAlertType: (AlertType) -> Unit
): String {
    val input = inputValue.toDoubleOrNull()
    if (input == null) {
        handleAlert(
            message = "Invalid input. Please enter a valid number.",
            type = AlertType.DANGER,
            showAlertCallback = showAlert,
            alertMessageCallback = setAlertMessage,
            alertTypeCallback = setAlertType
        )
        return "Invalid"
    }

    return when (category) {
        "Length" -> {
            val rates = mapOf(
                "Meter" to 1.0,
                "Centimeter" to 100.0,
                "Feet" to 3.28084,
                "Inch" to 39.3701,
                "Kilometer" to 0.001,
                "Micrometer" to 1_000_000.0,
                "Millimeter" to 1000.0,
                "Yard" to 1.09361
            )
            val sourceRate = rates[sourceUnit]
            if (sourceRate == null) {
                handleAlert(
                    message = "Invalid source unit selected.",
                    type = AlertType.DANGER,
                    showAlertCallback = showAlert,
                    alertMessageCallback = setAlertMessage,
                    alertTypeCallback = setAlertType
                )
                return "Invalid unit"
            }

            val targetRate = rates[targetUnit]
            if (targetRate == null) {
                handleAlert(
                    message = "Invalid target unit selected.",
                    type = AlertType.DANGER,
                    showAlertCallback = showAlert,
                    alertMessageCallback = setAlertMessage,
                    alertTypeCallback = setAlertType
                )
                return "Invalid unit"
            }

            val base = input / sourceRate
            val result = base * targetRate
            String.format(Locale.US, "%.4f", result)
        }
        "Duration" -> {
            // Similar changes as above for Duration conversion
            // (The pattern is the same)
            val rates = mapOf(
                "Seconds" to 1.0,
                "Minutes" to 60.0,
                "Hours" to 3600.0,
                "Days" to 86400.0,
                "Weeks" to 604800.0,
                "Months" to 2629746.0,
                "Years" to 31556952.0
            )

            val sourceRate = rates[sourceUnit]
            if (sourceRate == null) {
                handleAlert(
                    message = "Invalid source unit selected.",
                    type = AlertType.DANGER,
                    showAlertCallback = showAlert,
                    alertMessageCallback = setAlertMessage,
                    alertTypeCallback = setAlertType
                )
                return "Invalid unit"
            }

            val targetRate = rates[targetUnit]
            if (targetRate == null) {
                handleAlert(
                    message = "Invalid target unit selected.",
                    type = AlertType.DANGER,
                    showAlertCallback = showAlert,
                    alertMessageCallback = setAlertMessage,
                    alertTypeCallback = setAlertType
                )
                return "Invalid unit"
            }

            val baseInSeconds = input * sourceRate
            val result = baseInSeconds / targetRate

            String.format(Locale.US, "%.4f", result)
        }
        else -> {
            handleAlert(
                message = "Unsupported category: $category",
                type = AlertType.WARNING,
                showAlertCallback = showAlert,
                alertMessageCallback = setAlertMessage,
                alertTypeCallback = setAlertType
            )
            "Unsupported category"
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SetupUIPreview() {
    UnitConverterTheme {
        val previewSnackbarHostState = remember { SnackbarHostState() }
        UnitConverter(
            modifier = Modifier.padding(6.dp),
            snackbarHostState = previewSnackbarHostState
        )
    }
}