package com.efe.iso8583tools.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.efe.iso8583tools.IsoMessageComponent.DataElement
import com.efe.iso8583tools.IsoMessageComponent.SubIsoMessage
import com.efe.iso8583tools.ParsedIsoMessage
import com.efe.iso8583tools.parseIsoMessage
import com.efe.iso8583tools.parsedIsoMsg
import com.efe.iso8583tools.theme.AppTheme


@Composable
fun HomeScreen(isDarkTheme: Boolean, modifier: Modifier = Modifier, onThemeChanged: () -> Unit) {
    Column(modifier) {
        Header(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            isDarkTheme = isDarkTheme,
            onThemeChanged = onThemeChanged
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                IsoMessageInput(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onParseClicked = { isoMsgText ->
                        parseIsoMessage(isoMsgText)
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                IsoMessageFields(parsedIsoMessage = parsedIsoMsg.collectAsStateWithLifecycle().value)
            }
        }
    }
}

@Composable
private fun Header(isDarkTheme: Boolean, modifier: Modifier = Modifier, onThemeChanged: () -> Unit) {
    Row(modifier.background(MaterialTheme.colorScheme.primary).padding(10.dp)) {
        Icon(Icons.Default.Code, tint = MaterialTheme.colorScheme.onPrimary, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "ISO8583 Parser",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Parse and analyze ISO8583 messages",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.weight(2f))
        ThemeSwitcher(
            isDarkTheme = isDarkTheme,
            onThemeChanged = onThemeChanged,
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Composable
private fun IsoMessageInput(onParseClicked: (isoMessage: String) -> Unit, modifier: Modifier = Modifier) {
    var isoMessageText: String by rememberSaveable { mutableStateOf("") }

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ISO8583 Message",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    singleLine = true,
                    placeholder = {
                        Text("Enter ISO8583 Message")
                    },
                    modifier = Modifier.weight(0.8f),
                    value = isoMessageText,
                    onValueChange = { newValue ->
                        isoMessageText = newValue
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            onParseClicked(isoMessageText)
                        }
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    modifier = Modifier.weight(0.2f).wrapContentWidth().fillMaxHeight(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = isoMessageText.isNotBlank(),
                    onClick = {
                        onParseClicked(isoMessageText)
                    }
                ) {
                    Icon(Icons.Filled.Expand, contentDescription = "Parse")
                    Text(text = "Parse")
                }
            }

        }
    }
}

@Composable
private fun ThemeSwitcher(isDarkTheme: Boolean, onThemeChanged: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Switch(checked = isDarkTheme,
            onCheckedChange = { onThemeChanged() },
            thumbContent = {
                AnimatedVisibility(
                    visible = isDarkTheme,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    Icon(Icons.Filled.DarkMode, contentDescription = "Dark Theme")
                }

                AnimatedVisibility(
                    visible = !isDarkTheme,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    Icon(Icons.Filled.LightMode, contentDescription = "Light Theme")

                }
            })

    }
}

@Composable
private fun IsoMessageFields(parsedIsoMessage: ParsedIsoMessage?, modifier: Modifier = Modifier) {
    parsedIsoMessage ?: return
    AnimatedVisibility(visible = true) {
        Column(modifier = modifier) {
            MTICard(mti = parsedIsoMessage.mti, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            Card {
                LazyColumn {
                    items(parsedIsoMessage.isoMessageComponents) { item ->
                        Column {
                            when (item) {
                                is DataElement -> IsoMessageItemCard(
                                    dataElement = item,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                is SubIsoMessage -> SubIsoMessageItemCard(
                                    dataElements = item.dataElements,
                                    extendedFieldIndex = item.parentFieldIndex,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MTICard(mti: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Message Type Indicator (MTI)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    shape = RoundedCornerShape(6.dp)
                ).padding(8.dp)
            ) {
                Text(
                    text = mti,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun IsoMessageItemCard(dataElement: DataElement, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = dataElement.dataElementFieldName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = Icons.Filled.Info, contentDescription = "info")

                Spacer(modifier = Modifier.weight(2f))

                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(6.dp)
                    ).padding(8.dp)
                ) {
                    Text(text = "Length: ${dataElement.length}")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceDim,
                        shape = RoundedCornerShape(6.dp)
                    ).padding(8.dp)
                ) {
                    Text(text = dataElement.value)
                }
            }
        }
    }
}


@Composable
private fun SubIsoMessageItemCard(
    extendedFieldIndex: String,
    dataElements: List<DataElement>,
    modifier: Modifier = Modifier
) {
    var showSubFields by remember {
        mutableStateOf(true)
    }
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            IconButton(onClick = { showSubFields = !showSubFields }) {
                Icon(
                    imageVector = if (showSubFields) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
            Text(
                text = "Extended Field: $extendedFieldIndex", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = showSubFields) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(start = 16.dp)
                ) {
                    VerticalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        dataElements.forEach { dataElement ->
                            IsoMessageItemCard(dataElement = dataElement)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }

                }
            }
        }
    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(modifier = Modifier.fillMaxSize(), isDarkTheme = false, onThemeChanged = {})
    }
}

@Preview
@Composable
private fun MTIPreview() {
    AppTheme {
        MTICard(mti = "0800", modifier = Modifier.wrapContentHeight().padding(10.dp))
    }
}

@Composable
@Preview
private fun IsoMessageItemCardPreview() {
    IsoMessageItemCard(
        dataElement = DataElement(
            fieldIndex = "2",
            dataElementName = "Primary Account Number",
            value = "5389293948481939",
            length = 16,
            description = "Card Number"
        ),
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp)
    )
}
