package ru.gdcn.tycoon.storage.entity

import java.sql.Timestamp
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
    lateinit var password: ByteArray

    @Column(unique = true, nullable = false)
    lateinit var salt: ByteArray

    @Column(nullable = false)
    var role: Int = -1

    @Column(nullable = false)
    lateinit var registered: Timestamp

    constructor(username: String, password: ByteArray, salt: ByteArray, role: Int, registeredTimestamp: Timestamp) : this() {
        this.username = username
        this.password = password
        this.salt = salt
        this.role = role
        this.registered = registeredTimestamp
    }
}
