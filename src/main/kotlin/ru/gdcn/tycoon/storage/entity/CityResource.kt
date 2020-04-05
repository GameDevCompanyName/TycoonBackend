package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "t_city_resource")
class CityResource {

    @EmbeddedId
    @Column(nullable = false)
    lateinit var compositeId: CompositeKeyCityResource

    @Range(min = 0)
    @Column(nullable = false)
    var cost: Long = -1

    @Range(min = 0)
    @Column(nullable = false)
    var quantity: Long = -1

    @Transient
    var name: String = "unknown"
}
