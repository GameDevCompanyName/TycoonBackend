package ru.gdcn.tycoon.storage.entity

import org.hibernate.validator.constraints.Range
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.JoinColumn

@Embeddable
class CompositeKeyPlayerResource : Serializable {
    @Range(min = 0)
    @Column(name = "player_id", nullable = false)
    @JoinColumn(name = "player_id")
    var playerId: Long = -1

    @Range(min = 0)
    @Column(name = "resource_id", nullable = false)
    @JoinColumn(name = "resource_id")
    var resourceId: Long = -1

    constructor(playerId: Long, resourceId: Long) {
        this.playerId = playerId
        this.resourceId = resourceId
    }
}