package com.propellerads

data class ResponseData(
    val cur: String? = null,
    val id: String? = null,
    val seatbid: List<SeatbidItem?>? = null,
    val bidid: String? = null
)

data class Ext(
    val categories: List<Int?>? = null
)

data class BidItem(
    val ext: Ext? = null,
    val crid: String? = null,
    val h: Int? = null,
    val adm: String? = null,
    val mtype: Int? = null,
    val nurl: String? = null,
    val adid: String? = null,
    val adomain: List<String?>? = null,
    val price: Any? = null,
    val iurl: String? = null,
    val cat: List<String?>? = null,
    val w: Int? = null,
    val id: String? = null,
    val impid: String? = null,
    val cid: String? = null
)

data class SeatbidItem(
    val bid: List<BidItem?>? = null
)

