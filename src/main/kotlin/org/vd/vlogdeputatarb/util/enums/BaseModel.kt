package org.vd.vlogdeputatarb.util.enums

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?=null
)