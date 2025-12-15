package edu.uc.intprog32.rhythmhub.data.util

import java.util.UUID

object AvatarGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
