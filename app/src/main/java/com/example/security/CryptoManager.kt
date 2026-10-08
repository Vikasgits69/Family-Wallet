package com.example.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoManager {

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "FamilyVaultMasterKey_v1"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
        }

        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypts a byte array using AES-256-GCM.
     * Output format: [4 bytes: IV length] + [IV bytes] + [Ciphertext with GCM tag]
     */
    fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = getOrCreateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data)

        val byteBuffer = ByteBuffer.allocate(4 + iv.size + ciphertext.size)
        byteBuffer.putInt(iv.size)
        byteBuffer.put(iv)
        byteBuffer.put(ciphertext)
        return byteBuffer.array()
    }

    /**
     * Decrypts an encrypted byte array.
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        val byteBuffer = ByteBuffer.wrap(encryptedData)
        val ivLength = byteBuffer.int
        if (ivLength < 12 || ivLength > 16) {
            throw IllegalArgumentException("Invalid IV length: $ivLength")
        }
        val iv = ByteArray(ivLength)
        byteBuffer.get(iv)

        val ciphertext = ByteArray(byteBuffer.remaining())
        byteBuffer.get(ciphertext)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = getOrCreateSecretKey()
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(ciphertext)
    }
}
