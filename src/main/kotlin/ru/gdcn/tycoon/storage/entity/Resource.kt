package ru.gdcn.tycoon.storage.entity

import javax.persistence.*

@Entity
@Table(name = "t_resource")
class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var name: String
}
