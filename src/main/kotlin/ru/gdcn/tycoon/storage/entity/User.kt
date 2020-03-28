package ru.gdcn.tycoon.storage.entity

import javax.persistence.*

import kotlin.jvm.Transient

@Entity
@Table(name = "t_user")
class User() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var username: String

    @Column(nullable = false)
    lateinit var password: String

    @Column(unique = true, nullable = false)
    lateinit var salt: String

    @Column(nullable = false)
    var role: Int = -1

    @Transient
    lateinit var passwordConfirm: String

    constructor(username: String, password: String, salt: String, role: Int) : this() {
        this.username = username
        this.password = password
        this.salt = salt
        this.role = role
    }
}
