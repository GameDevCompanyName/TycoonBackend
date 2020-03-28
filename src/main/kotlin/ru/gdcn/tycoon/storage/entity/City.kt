package ru.gdcn.tycoon.storage.entity

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

    @Transient
    var players: MutableSet<String> = mutableSetOf()
}
