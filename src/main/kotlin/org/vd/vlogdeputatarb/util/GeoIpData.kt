package org.vd.vlogdeputatarb.util

data class GeoIpData(
    val city: String?,
    val country: String?,
    val asn: Long?,
    val providerIp: String?
)