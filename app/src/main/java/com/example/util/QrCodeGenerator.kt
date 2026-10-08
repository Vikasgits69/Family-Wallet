package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object QrCodeGenerator {

    fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            }
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Builds standard Indian UPI Payment URI compatible with GPay, PhonePe, Paytm, BHIM, CRED
     * Format: upi://pay?pa=VPA&pn=NAME&cu=INR&am=AMOUNT
     */
    fun buildUpiUri(vpa: String, payeeName: String, amount: Double? = null): String {
        val cleanVpa = vpa.trim()
        val cleanName = payeeName.trim().ifBlank { "Family Vault Payee" }
        val sb = StringBuilder("upi://pay?pa=$cleanVpa&pn=${cleanName.replace(" ", "%20")}&cu=INR")
        if (amount != null && amount > 0) {
            sb.append("&am=%.2f".format(amount))
        }
        return sb.toString()
    }
}
