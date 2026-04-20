package org.vd.vlogdeputatarb.util.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.data.article.Block

@Component
class BlockMapper(
    private val objectMapper: ObjectMapper
) {
    fun toBlock(json: String): List<Block> =
        objectMapper.readValue(json, object : TypeReference<List<Block>>() {})

    fun toJson(blocks: List<Block>): String =
        objectMapper.writeValueAsString(blocks)
}