package org.vd.vlogdeputatarb.data.tag

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.vd.vlogdeputatarb.util.enums.BaseModel
import org.vd.vlogdeputatarb.util.enums.Language


//@Table(name = "tags",uniqueConstraints = [UniqueConstraint(columnNames = ["code"])])
//@Entity
//class Tag(
//    @Column(unique = true, nullable = false)
//    val code: String= ""
//
//): BaseModel(){
//    constructor() : this("")
//}


@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["code", "language"]),
        UniqueConstraint(columnNames = ["name", "language"])
    ]
)@Entity
class Tag(
    @Column( nullable = false)
    val code: String,
    @Enumerated(EnumType.STRING)
    val language: Language,
    @Column( nullable = false)
    val name: String
) : BaseModel()