package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.JoinColumn

@Embeddable
class CompositeKeyCityResource : Serializable{

    @Range(min = 0)
    @Column(nullable = false)
    @JoinColumn(name = "city_id")
    var cityId: Long = -1

    @Range(min = 0)
    @Column(nullable = false)
    @JoinColumn(name = "resource_id")
    var resourceId: Long = -1

    constructor(cityId: Long, resourceId: Long) {
        this.cityId = cityId
        this.resourceId = resourceId
    }
}
