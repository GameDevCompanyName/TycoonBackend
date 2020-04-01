package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "t_city")
class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @Column(nullable = false)
    @Range(min = 0, max = 16_777_215)
    var color: Int = -1

    @Column(nullable = false)
    @Range(min = 0)
    var population: Int = 0

    @Column(name = "polygon_data", nullable = false)
    lateinit var polygonData: String

    @Transient
    var players: MutableSet<String> = mutableSetOf()
}
