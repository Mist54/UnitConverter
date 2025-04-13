package com.example.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unitconverter.ui.theme.UnitConverterTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterTheme {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Unit converter") })}
                ){
                    paddingValues ->
                    UnitConverter(modifier = Modifier.padding(paddingValues))
                }
            }
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverter(modifier: Modifier){
    /* this section is for the category of main section
    * hard coded for now. we can use free api for handling the conversions and setup. */
    val categories  = listOf("Length", "Currency","Duration", "Angle")
    var selectedCategory by remember { mutableStateOf(categories .first()) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    //unit of length - updated in future with different Units like duration and Angle.
    val lengthUnits = listOf("Meter", "Centimeter", "Feet", "Inch")

    //used for sub dropdowns
    var selectedSourceUnit by remember { mutableStateOf(lengthUnits.first()) }
    var isSourceUnitMenuExpanded by remember { mutableStateOf(false) }

    var selectedTargetUnit by remember { mutableStateOf(lengthUnits[1]); }
    var isTargetUnitMenuExpanded by remember { mutableStateOf(false) }

    // input and result section
    var inputValue by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }




    //UI setup
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.padding(0.dp,20.dp))
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
                    DropdownMenuItem(text = { Text(text = category )}, onClick = {
                        selectedCategory = category
                        isCategoryMenuExpanded = false})
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
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSourceUnitMenuExpanded)

                    }
                )
                ExposedDropdownMenu(
                    expanded = isSourceUnitMenuExpanded,
                    onDismissRequest = {isSourceUnitMenuExpanded = false},

                ) {
                    lengthUnits.forEach { lengthUnit ->
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
                    lengthUnits.forEach { lengthUnit ->
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
        //button field
        Button(
            onClick = {
                val conversionResult = convertUnit(
                    category = selectedCategory,
                    inputValue = inputValue,
                    sourceUnit = selectedSourceUnit,
                    targetUnit = selectedTargetUnit
                )
                // Update the result with the conversion result
                result = conversionResult
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Convert")



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
    inputValue:String,
    sourceUnit:String,
    targetUnit:String
):String{
    val input = inputValue.toDoubleOrNull() ?: return "Invalid input"
    return when(category){
        "Length" -> {
            val rates = mapOf(
                "Meter" to 1.0,
                "Centimeter" to 100.0,
                "Feet" to 3.28084,
                "Inch" to 39.3701
            )
            val sourceRate = rates[sourceUnit] ?: return "Invalid unit"
            val targetRate = rates[targetUnit] ?: return "Invalid unit"
            val base = input / sourceRate
            val result = base * targetRate
            String.format(Locale.US, "%.4f", result)
        }
        else -> "Unsupported category"

    }




}

@Preview(showBackground = true)
@Composable
fun SetupUIPreview() {
    UnitConverterTheme {
        UnitConverter(modifier = Modifier.padding(6.dp))
    }
}