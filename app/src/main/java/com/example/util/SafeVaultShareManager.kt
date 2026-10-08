package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.BankAccount
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.Document
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

/**
 * Secure sharing and self-destructing clipboard manager for financial and identity assets.
 */
object SafeVaultShareManager {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingClearRunnable: Runnable? = null
    private var lastCopiedTextHash: Int = 0

    /**
     * Copies sensitive text (e.g. Card Number, CVV, Banking PIN/Password)
     * with an optional self-destructing timer that wipes the clipboard after [timeoutSeconds].
     */
    fun copyWithSecurity(
        context: Context,
        label: String,
        text: String,
        timeoutSeconds: Int = 30,
        enableAutoClear: Boolean = true,
        onCleared: (() -> Unit)? = null
    ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cleanText = if (label.contains("Card", ignoreCase = true) ||
            label.contains("Number", ignoreCase = true) ||
            label.contains("CVV", ignoreCase = true)
        ) {
            text.replace(" ", "").trim()
        } else {
            text.trim()
        }

        val clip = ClipData.newPlainText(label, cleanText)
        clipboard.setPrimaryClip(clip)
        lastCopiedTextHash = cleanText.hashCode()

        // Cancel any previous scheduled wipe
        pendingClearRunnable?.let { mainHandler.removeCallbacks(it) }

        if (enableAutoClear && timeoutSeconds > 0) {
            Toast.makeText(
                context,
                "$label copied! Auto-destructs in ${timeoutSeconds}s ⏱️",
                Toast.LENGTH_SHORT
            ).show()

            val clearRunnable = Runnable {
                try {
                    val currentClip = clipboard.primaryClip
                    if (currentClip != null && currentClip.itemCount > 0) {
                        val currentText = currentClip.getItemAt(0).text?.toString() ?: ""
                        if (currentText.hashCode() == lastCopiedTextHash) {
                            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                            Toast.makeText(
                                context.applicationContext,
                                "🛡️ Clipboard securely wiped for safety",
                                Toast.LENGTH_SHORT
                            ).show()
                            onCleared?.invoke()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            pendingClearRunnable = clearRunnable
            mainHandler.postDelayed(clearRunnable, timeoutSeconds * 1000L)
        } else {
            Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Builds a clean, professionally formatted text template for sharing bank account details.
     */
    fun shareBankAccountDetails(
        context: Context,
        account: BankAccount,
        includeBeneficiaryName: Boolean = true,
        beneficiaryName: String = ""
    ) {
        val sb = StringBuilder()
        sb.append("🏦 BANK ACCOUNT TRANSFER DETAILS\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        if (includeBeneficiaryName && beneficiaryName.isNotBlank()) {
            sb.append("Beneficiary Name: $beneficiaryName\n")
        }
        sb.append("Bank Name: ${account.bankName}\n")
        sb.append("Account Number: ${account.accountNumber}\n")
        sb.append("Account Type: ${account.accountType}\n")
        sb.append("IFSC Code: ${account.ifscCode}\n")
        if (account.branchName.isNotBlank()) {
            sb.append("Branch: ${account.branchName}\n")
        }
        if (account.micrCode.isNotBlank()) {
            sb.append("MICR Code: ${account.micrCode}\n")
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Shared securely via Family Wallet")

        shareText(context, "Share Bank Details", sb.toString())
    }

    /**
     * Builds a masked or clean format for sharing card payment credentials safely.
     */
    fun shareCardDetails(
        context: Context,
        bankName: String,
        cardType: String,
        last4: String,
        holderName: String,
        expiry: String,
        masked: Boolean = true
    ) {
        val sb = StringBuilder()
        sb.append("💳 $bankName $cardType\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Cardholder: $holderName\n")
        sb.append("Card Number: ${if (masked) "•••• •••• •••• $last4" else "Last 4: $last4"}\n")
        sb.append("Expiry: $expiry\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Shared via Family Wallet")

        shareText(context, "Share Card Details", sb.toString())
    }

    /**
     * Generates a watermarked version of an image file and initiates the Android Share Intent.
     * [watermarkText] defaults to "FOR VERIFICATION ONLY - [DATE]"
     */
    fun shareWatermarkedImage(
        context: Context,
        imageFile: File,
        watermarkText: String,
        customPurpose: String = ""
    ): Boolean {
        return try {
            if (!imageFile.exists()) return false

            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return false
            val watermarkedBitmap = applyWatermark(bitmap, watermarkText, customPurpose)

            // Save to shared_exports cache directory
            val exportDir = File(context.cacheDir, "shared_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val outputFile = File(exportDir, "watermarked_${System.currentTimeMillis()}_${imageFile.name}")
            FileOutputStream(outputFile).use { out ->
                watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }

            // Share via FileProvider
            val authority = "${context.packageName}.fileprovider"
            val contentUri: Uri = FileProvider.getUriForFile(context, authority, outputFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Secure Document Copy (${outputFile.name})")
                putExtra(Intent.EXTRA_TEXT, "Protected document scan. Watermark: '$watermarkText'")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Watermarked Document"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to create watermarked copy: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Applies repeated semi-transparent diagonal watermarking across the bitmap.
     */
    private fun applyWatermark(src: Bitmap, watermarkText: String, customPurpose: String): Bitmap {
        val result = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val width = result.width.toFloat()
        val height = result.height.toFloat()

        val paint = Paint().apply {
            color = Color.RED
            alpha = 75 // Semi-transparent
            textSize = (width / 22f).coerceIn(32f, 100f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            setShadowLayer(4f, 2f, 2f, Color.argb(120, 0, 0, 0))
        }

        val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val fullText = buildString {
            append(watermarkText.uppercase(Locale.getDefault()))
            if (customPurpose.isNotBlank()) {
                append(" • ").append(customPurpose.uppercase(Locale.getDefault()))
            }
            append(" • ").append(dateStr)
        }

        canvas.save()
        // Rotate 30 degrees for diagonal security pattern
        canvas.rotate(-30f, width / 2f, height / 2f)

        val stepY = paint.textSize * 4f
        var y = -height
        while (y < height * 2f) {
            canvas.drawText(fullText, width / 2f, y, paint)
            y += stepY
        }

        canvas.restore()

        // Also draw a bottom safety banner
        val bannerHeight = (height * 0.05f).coerceIn(60f, 140f)
        val bannerPaint = Paint().apply {
            color = Color.argb(180, 15, 23, 42) // Slate 900
            isAntiAlias = true
        }
        canvas.drawRect(0f, height - bannerHeight, width, height, bannerPaint)

        val bannerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = (bannerHeight * 0.38f).coerceIn(24f, 48f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "RESTRICTED COPY • $fullText",
            width / 2f,
            height - (bannerHeight * 0.35f),
            bannerTextPaint
        )

        return result
    }

    private fun shareText(context: Context, chooserTitle: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
