package com.efe.iso8583tools.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.efe.iso8583tools.DataElement
import com.efe.iso8583tools.IsoMessage
import com.efe.iso8583tools.parseIsoMessage
import com.efe.iso8583tools.parsedIsoMsg
import com.efe.iso8583tools.theme.AppTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier) {
        Header(modifier = Modifier.fillMaxWidth().wrapContentHeight())
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
                IsoMessgeFields(parsedIsoMessage = parsedIsoMsg.collectAsStateWithLifecycle().value)
            }
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(modifier.background(MaterialTheme.colorScheme.primary).padding(10.dp)) {
        Row {
            Icon(Icons.Default.Code, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "ISO8583 Parser",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Parse and analyze ISO8583 messages",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
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
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                OutlinedTextField(
                    placeholder = {
                        Text("Enter ISO8583 Message")
                    },
                    modifier = Modifier.weight(0.7f),
                    value = isoMessageText,
                    onValueChange = { newValue ->
                        isoMessageText = newValue
                    }
                )

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    modifier = Modifier.weight(0.2f),
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
private fun IsoMessgeFields(parsedIsoMessage: IsoMessage?, modifier: Modifier = Modifier) {
    parsedIsoMessage ?: return
    AnimatedVisibility(visible = true) {
        Column(modifier = modifier) {
            MTICard(mti = parsedIsoMessage.mti, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            Card {
                LazyColumn {
                    items(parsedIsoMessage.dataElements) { item: DataElement ->
                        Column {
                            IsoMessageItemCard(dataElement = item, modifier = Modifier.padding(0.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
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
            Row(modifier = Modifier) {
                Text(
                    text = dataElement.dataElementFieldName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(2f))

                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceDim,
                        shape = RoundedCornerShape(6.dp)
                    ).padding(8.dp)
                ) {
                    Text(text = "Length: ${dataElement.length}")
                }
            }

            Text(text = dataElement.description.orEmpty(), style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "info")
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


@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(modifier = Modifier.fillMaxSize())
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
            fieldIndex = 2,
            dataElementName = "Primary Account Number",
            value = "5389293948481939",
            length = 16,
            description = "Card Number"
        ),
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp)
    )
}