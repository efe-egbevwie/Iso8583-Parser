package com.efe.iso8583tools

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jpos.iso.*
import org.jpos.iso.packager.GenericPackager
import java.lang.reflect.Field

private val parsedIsoMsgStateFlow = MutableStateFlow<ParsedIsoMessage?>(null)
val parsedIsoMsg: StateFlow<ParsedIsoMessage?> = parsedIsoMsgStateFlow.asStateFlow()

fun parseIsoMessage(isoMessageText: String) {
    try {
        val isoMsg = ISOMsg().apply {
            packager = GenericPackager(javaClass.classLoader.getResourceAsStream("nibss.xml"))
        }
        val isoMsgBytes = isoMessageText.toByteArray()
        isoMsg.unpack(isoMsgBytes)
        isoMsg.dump(System.out, " ")
        val fields: Field = ISOMsg::class.java.getDeclaredField("fields")
        fields.isAccessible = true
        val isoMsgDataElements = (fields.get(isoMsg) as Map<Int, Any>)
        val isoMessageComponents: List<IsoMessageComponent> = buildList {
            for (index in 2..128) {
                if (isoMsg.hasField(index)) {
                    val isoComponent: ISOComponent = isoMsgDataElements[index] as ISOComponent
                    when (isoComponent) {
                        is ISOBinaryField -> {
                            val value = ISOUtil.hexString(isoComponent.bytes)
                            val dataElementName = getDataElementNameByIndex(index)
                            val length = value.length
                            add(
                                IsoMessageComponent.DataElement(
                                    fieldIndex = index.toString(),
                                    dataElementName = dataElementName.orEmpty(),
                                    value = value,
                                    length = length,
                                    description = ""
                                )
                            )
                        }

                        is ISOField -> {
                            val value = isoComponent.value as? String ?: ""
                            val dataElementName = getDataElementNameByIndex(index)
                            val length = value.length
                            add(
                                IsoMessageComponent.DataElement(
                                    fieldIndex = index.toString(),
                                    dataElementName = dataElementName.orEmpty(),
                                    value = value,
                                    length = length,
                                    description = ""
                                )
                            )
                        }

                        is ISOMsg -> {
                            val parentFieldIndex = index.toString()
                            val subIsoMsgFields: Field = ISOMsg::class.java.getDeclaredField("fields")
                            subIsoMsgFields.isAccessible = true
                            val subIsoMsgDataElements = (subIsoMsgFields.get(isoComponent) as Map<Int, Any>)
                            val subDataElements: MutableList<IsoMessageComponent.DataElement> = mutableListOf()
                            subIsoMsgDataElements.keys.forEach { subIndex ->
                                val rawElement = subIsoMsgDataElements[subIndex]
                                if (rawElement is ISOBitMap) return@forEach
                                val dataElement = rawElement as ISOField
                                val value = dataElement.value as? String ?: ""
                                val dataElementName = getDataElementNameByIndex(subIndex)
                                val length = value.length
                                subDataElements.add(
                                    IsoMessageComponent.DataElement(
                                        fieldIndex = parentFieldIndex.plus(".${subIndex}"),
                                        dataElementName = dataElementName.orEmpty(),
                                        value = value,
                                        length = length,
                                        description = ""
                                    )
                                )
                            }
                            add(
                                IsoMessageComponent.SubIsoMessage(
                                    parentFieldIndex = parentFieldIndex,
                                    dataElements = subDataElements
                                )
                            )
                        }

                        else -> {
                            val value = isoComponent.value as? String ?: ""
                            val dataElementName = getDataElementNameByIndex(index)
                            val length = value.length
                            add(
                                IsoMessageComponent.DataElement(
                                    fieldIndex = index.toString(),
                                    dataElementName = dataElementName.orEmpty(),
                                    value = value,
                                    length = length,
                                    description = ""
                                )
                            )
                        }

                    }
                }
            }
        }
        parsedIsoMsgStateFlow.update { ParsedIsoMessage(mti = isoMsg.mti, isoMessageComponents = isoMessageComponents) }
    } catch (e: Exception) {
        println("exception parsing message -> $e")
        e.printStackTrace()
    }
}

data class ParsedIsoMessage(
    val mti: String,
    val isoMessageComponents: List<IsoMessageComponent>
)


sealed class IsoMessageComponent {
    data class DataElement(
        val fieldIndex: String,
        val dataElementName: String,
        val value: String,
        val length: Int,
        val description: String? = null
    ) : IsoMessageComponent() {
        val dataElementFieldName get() = "Field $fieldIndex: $dataElementName"
    }

    data class SubIsoMessage(
        val parentFieldIndex: String,
        val dataElements: List<DataElement>
    ) : IsoMessageComponent()
}


private val dataElementNames = mapOf(
    0 to "MESSAGE TYPE INDICATOR",
    1 to "BIT MAP",
    2 to "PAN - PRIMARY ACCOUNT NUMBER",
    3 to "PROCESSING CODE",
    4 to "AMOUNT, TRANSACTION",
    5 to "AMOUNT, SETTLEMENT",
    7 to "TRANSMISSION DATE AND TIME",
    9 to "CONVERSION RATE, SETTLEMENT",
    11 to "SYSTEM TRACE AUDIT NUMBER",
    12 to "TIME, LOCAL TRANSACTION",
    13 to "DATE, LOCAL TRANSACTION",
    14 to "DATE, EXPIRATION",
    15 to "DATE, SETTLEMENT",
    16 to "DATE, CONVERSION",
    18 to "MERCHANTS TYPE",
    22 to "POINT OF SERVICE ENTRY MODE",
    23 to "CARD SEQUENCE NUMBER",
    25 to "POINT OF SERVICE CONDITION CODE",
    26 to "POINT OF SERVICE PIN CAPTURE CODE",
    28 to "AMOUNT, TRANSACTION FEE",
    29 to "AMOUNT, SETTLEMENT FEE",
    30 to "AMOUNT, TRANSACTION PROCESSING FEE",
    31 to "AMOUNT, SETTLEMENT PROCESSING FEE",
    32 to "ACQUIRING INSTITUTION IDENT CODE",
    33 to "FORWARDING INSTITUTION IDENT CODE",
    34 to "PAN EXTENDED",
    35 to "TRACK 2 DATA",
    37 to "RETRIEVAL REFERENCE NUMBER",
    38 to "AUTHORIZATION IDENTIFICATION RESPONSE",
    39 to "RESPONSE CODE",
    40 to "SERVICE RESTRICTION CODE",
    41 to "CARD ACCEPTOR TERMINAL IDENTIFICACION",
    42 to "CARD ACCEPTOR IDENTIFICATION CODE",
    43 to "CARD ACCEPTOR NAME/LOCATION",
    44 to "ADITIONAL RESPONSE DATA",
    45 to "TRACK 1 DATA",
    48 to "ADITIONAL DATA - PRIVATE",
    49 to "CURRENCY CODE, TRANSACTION",
    50 to "CURRENCY CODE, SETTLEMENT",
    51 to "CURRENCY CODE, CARDHOLDER",
    52 to "PIN DATA",
    53 to "SECURITY RELATED CONTROL INFORMATION",
    54 to "ADDITIONAL AMOUNTS",
    55 to "INTEGRATED CIRCUIT CARD SYSTEM RELATED DATA",
    56 to "MESSAGE REASON CODE",
    58 to "AUTHORIZING AGENT CODE",
    59 to "ECHO DATA",
    60 to "PAYMENT INFORMATION",
    62 to "PRIVATE FIELD, MANAGEMENT DATA 1",
    63 to "PRIVATE FIELD, MANAGEMENT DATA 2",
    64 to "MESSAGE AUTHENTICATION CODE FIELD",
    67 to "EXTENDED PAYMENT CODE",
    90 to "ORIGINAL DATA ELEMENTS",
    91 to "FILE UPDATE CODE",
    95 to "REPLACEMENT AMOUNTS",
    98 to "PAYEE",
    100 to "RECEIVING INSTITUTION IDENT CODE",
    102 to "ACCOUNT IDENTIFICATION 1",
    103 to "ACCOUNT IDENTIFICATION 2",
    123 to "POS DATA CODE",
    124 to "NEAR FIELD COMMUNICATION DATA",
    128 to "MAC 2"
)

private fun getDataElementNameByIndex(index: Int): String? {
    return dataElementNames.getOrDefault(index, null)
}