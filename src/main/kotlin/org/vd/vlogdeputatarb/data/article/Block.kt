package org.vd.vlogdeputatarb.data.article

import org.vd.vlogdeputatarb.util.enums.BlockType

data class Block (
    val type: BlockType,
    val content: String? = null
)