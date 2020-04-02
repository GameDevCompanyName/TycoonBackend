package ru.gdcn.tycoon.util

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object HashingHelper {
    /**
     * @return Optional<Pair<HashedPassword, Salt>>
     */
    fun hashPassword(password: String, salt: ByteArray? = null): Optional<Pair<ByteArray, ByteArray>> {
        val newSalt: ByteArray
        if (salt == null) {
            val random = SecureRandom()
            newSalt = ByteArray(16)
            random.nextBytes(newSalt)
        } else {
            newSalt = salt
        }

        val iterationCount: Int = EnvReader.env[EnvReader.KEY_SERVER_HASHING_PASS_ITERATION_COUNT]?.toInt() ?: 65536
        val keyLength: Int = EnvReader.env[EnvReader.KEY_SERVER_HASHING_PASS_KEY_LENGTH]?.toInt() ?: 128

        val spec = PBEKeySpec(password.toCharArray(), newSalt, iterationCount, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        try {
            val hashedPassword = factory.generateSecret(spec).encoded
            return Optional.of(Pair(hashedPassword, newSalt))
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.empty()
        }
    }

    fun equalsPassword(hashedPassword: ByteArray, noHashedPassword: String, salt: ByteArray): Boolean {
        val tempHash = hashPassword(noHashedPassword, salt)
        return if (tempHash.isEmpty) {
            false
        } else {
            hashedPassword.contentEquals(tempHash.get().first)
        }
    }
}