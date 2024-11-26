package com.efe.iso8583tools

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jpos.iso.ISOMsg
import java.lang.reflect.Field

private val parsedIsoMsgStateFlow = MutableStateFlow<IsoMessage?>(null)
val parsedIsoMsg: StateFlow<IsoMessage?> = parsedIsoMsgStateFlow.asStateFlow()

fun parseIsoMessage(isoMessageText: String) {
    try {
        val isoMsg = ISOMsg().apply {
            packager = NibssPackager2()
        }
        val isoMsgBytes = isoMessageText.toByteArray()
        isoMsg.unpack(isoMsgBytes)
        isoMsg.dump(System.out, " ")
        val fields: Field = ISOMsg::class.java.getDeclaredField("fields")
        fields.isAccessible = true
        val isoMsgDataElements = (fields.get(isoMsg) as Map<Int, Any>)
        val dataElementss = buildList {
            for(i in  2..128){
                if (isoMsg.hasField(i)){
                    val value = isoMsg.getValue(i).toString()
                    val dataElementName = getDataElementNameByIndex(i)
                    val length = value.length
                    add(
                        DataElement(
                            fieldIndex = i,
                            dataElementName = dataElementName.orEmpty(),
                            value = value,
                            length = length,
                            description = ""
                        )
                    )
                    println("isoValue -> $value")
                }
            }
        }
        val isoMessage = IsoMessage(
            mti = isoMsg.mti,
            dataElements = dataElementss
        )
        parsedIsoMsgStateFlow.update { isoMessage }
    }catch (e:Exception){
        println("exception parsing message -> $e")
    }
}

data class IsoMessage(val mti: String, val dataElements: List<DataElement>)

data class DataElement(
    val fieldIndex: Int,
    val dataElementName: String,
    val value: String,
    val length: Int,
    val description: String? = null
) {
    val dataElementFieldName get() = "Field $fieldIndex: $dataElementName"
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