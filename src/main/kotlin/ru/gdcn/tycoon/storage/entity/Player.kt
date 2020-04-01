package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import javax.persistence.*

@Entity
@Table(name = "t_player")
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @Range(min = 0)
    @Column(nullable = false)
    var money: Long = 0

    @Column(name = "city_id", nullable = false)
    @JoinColumn(name = "city_id")
    var cityId: Long = -1

    @Column(name = "user_id", unique = true, nullable = false)
    @JoinColumn(name = "user_id")
    var userId: Long = -1

    constructor(name: String, money: Long, cityId: Long, userId: Long) : this() {
        this.name = name
        this.money = money
        this.userId = userId
        this.cityId = cityId
    }
}
