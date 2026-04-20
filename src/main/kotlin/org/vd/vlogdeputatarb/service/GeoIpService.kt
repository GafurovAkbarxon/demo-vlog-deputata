package org.vd.vlogdeputatarb.service

import com.maxmind.geoip2.DatabaseReader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.util.GeoIpData
import java.net.InetAddress
@Service
class GeoIpService(
    @Qualifier("cityDatabaseReader")
    private val cityReader: DatabaseReader,
    @Qualifier("asnDatabaseReader")
    private val asnReader: DatabaseReader
) {

    fun resolve(ip: String): GeoIpData {
        return try {
            val inet = InetAddress.getByName(ip)

            val city = cityReader.city(inet)
            val asn = asnReader.asn(inet)
println("city: ${city.city.name}")
println("country: ${city.country.name}")
println("asn: ${asn.autonomousSystemNumber}")
println("provider: ${asn.autonomousSystemOrganization}")
            GeoIpData(
                city = city.city.name,
                country = city.country.name,
                asn = asn.autonomousSystemNumber,
                providerIp = asn.autonomousSystemOrganization
            )
        } catch (e: Exception) {
            GeoIpData(null, null, null, null)
        }
    }
}