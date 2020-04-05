package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "t_player_resource")
class PlayerResource() {

    @EmbeddedId
    @Column(nullable = false)
    lateinit var compositeId: CompositeKeyPlayerResource

    @Range(min = 0)
    @Column(nullable = false)
    var quantity: Long = -1

    @Transient
    var name: String = "unknown"

    constructor(resourceId: Long, playerId: Long, quantity: Long) : this() {
        this.compositeId = CompositeKeyPlayerResource(playerId, resourceId)
        this.quantity = quantity
    }
}
