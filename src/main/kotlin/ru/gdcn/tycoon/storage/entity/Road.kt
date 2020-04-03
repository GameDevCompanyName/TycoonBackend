package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import java.io.Serializable
import javax.persistence.*

@Embeddable
class CompositeKeyRoad() : Serializable{
    @Range(min = 0)
    @Column(name = "from_city_id", nullable = false)
    @JoinColumn(name = "city_id")
    var fromCityId: Long = -1

    @Range(min = 0)
    @Column(name = "to_city_id", nullable = false)
    @JoinColumn(name = "city_id")
    var toCityId: Long = -1

    constructor(fromCityId: Long, toCityId: Long) : this() {
        this.fromCityId = fromCityId
        this.toCityId = toCityId
    }
}

@Entity
@Table(name = "t_road")
class Road {
    @EmbeddedId
    @Column(nullable = false)
    lateinit var compositeId: CompositeKeyRoad
}
