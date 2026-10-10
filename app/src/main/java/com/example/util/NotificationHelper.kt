package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.CreditCard
import com.example.data.Document
import com.example.data.WalletOrGiftCard
import com.example.data.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_BILLS = "channel_credit_card_bills"
    const val CHANNEL_EXPIRIES = "channel_card_expiries"
    const val CHANNEL_GENERAL = "channel_vault_general"

    private const val ACTION_REMINDER_CHECK = "com.example.familywallet.REMINDER_CHECK"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val billsChannel = NotificationChannel(
                CHANNEL_BILLS,
                "Credit Card Bills & Due Dates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts for credit card statement bill generation and payment due dates"
                enableVibration(true)
            }

            val expiriesChannel = NotificationChannel(
                CHANNEL_EXPIRIES,
                "Card & Voucher Expiries",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders when gift cards, credit cards, or documents are nearing expiration"
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Vault Reminders & Security",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General security checkups, sync alerts, and test notifications"
            }

            notificationManager.createNotificationChannel(billsChannel)
            notificationManager.createNotificationChannel(expiriesChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun sendTestNotification(context: Context) {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Vault Reminders Active 🛡️")
            .setContentText("Notifications and alerts are successfully enabled for Family Wallet!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your credit card statement dates, payment due dates, and gift card expiries will now alert you in advance."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(9999, notification)
        } catch (e: SecurityException) {
            // Ignored if permission was revoked
        }
    }

    fun sendBillReminderNotification(
        context: Context,
        cardName: String,
        memberName: String,
        alertTitle: String,
        alertText: String,
        notificationId: Int
    ) {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BILLS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("$cardName • $alertTitle")
            .setContentText(alertText)
            .setSubText(memberName)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$alertText (Family Member: $memberName)"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Ignored
        }
    }

    fun sendExpiryNotification(
        context: Context,
        title: String,
        body: String,
        notificationId: Int
    ) {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EXPIRIES)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Ignored
        }
    }

    /**
     * Inspects all active credit cards, gift cards, and documents,
     * triggering real notifications for bill dates, payment due dates, and expiries.
     */
    fun checkAndTriggerDueReminders(
        context: Context,
        creditCards: List<CreditCard>,
        wallets: List<WalletOrGiftCard>,
        documents: List<Document>
    ): Int {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return 0
        var notificationsSent = 0
        val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        // 1. Credit Cards Check
        creditCards.forEachIndexed { index, card ->
            // Check Statement Bill Date
            if (card.remindBillDate && card.statementDate.isNotBlank()) {
                val billDay = parseDayOfMonth(card.statementDate)
                if (billDay != null && (billDay == currentDayOfMonth || Math.abs(billDay - currentDayOfMonth) <= 2)) {
                    val message = "Statement bill date is ${card.statementDate}. Log your statement cycle expenses & payments."
                    sendBillReminderNotification(
                        context = context,
                        cardName = card.cardName,
                        memberName = card.cardholderName.ifBlank { "Personal" },
                        alertTitle = "Bill Date Alert",
                        alertText = message,
                        notificationId = 1000 + index * 2
                    )
                    notificationsSent++
                }
            }

            // Check Payment Due Date
            if (card.remindDueDate && card.dueDate.isNotBlank() && !card.isBillPaid) {
                val dueDay = parseDayOfMonth(card.dueDate)
                if (dueDay != null && (dueDay == currentDayOfMonth || Math.abs(dueDay - currentDayOfMonth) <= 3)) {
                    val message = "Payment due date is ${card.dueDate}! Avoid late charges and pay on time."
                    sendBillReminderNotification(
                        context = context,
                        cardName = card.cardName,
                        memberName = card.cardholderName.ifBlank { "Personal" },
                        alertTitle = "Payment Due Date Alert",
                        alertText = message,
                        notificationId = 1000 + index * 2 + 1
                    )
                    notificationsSent++
                }
            }
        }

        // 2. Gift Cards Expiry Check (Exclude cards marked as used)
        wallets.filter { it.isGiftCard && !it.isMarkedAsUsed && it.remindExpiry && it.expiryDate.isNotBlank() }
            .forEachIndexed { idx, gc ->
                val daysLeft = calculateDaysUntilDate(gc.expiryDate)
                if (daysLeft != null && daysLeft in -1..14) {
                    val title = if (daysLeft < 0) "Gift Card Expired: ${gc.providerOrName}"
                    else if (daysLeft == 0L) "Gift Card Expires Today: ${gc.providerOrName} ⚠️"
                    else "Gift Card Expiring Soon: ${gc.providerOrName}"
                    val bal = if (gc.currentBalance > 0) gc.currentBalance.toInt() else gc.amount.toInt()
                    val body = if (daysLeft < 0) "Expired on ${gc.expiryDate}. Remaining balance: ₹$bal."
                    else if (daysLeft == 0L) "Expires TODAY! Redeem your remaining balance of ₹$bal immediately."
                    else "Remaining balance ₹$bal expires on ${gc.expiryDate} ($daysLeft days left). Redeem before it lapses!"
                    sendExpiryNotification(
                        context = context,
                        title = title,
                        body = body,
                        notificationId = 3000 + idx
                    )
                    notificationsSent++
                }
            }

        // 3. Personal Documents Expiry Check
        documents.filter { it.expiryDate != null }.forEachIndexed { idx, doc ->
            val days = doc.daysUntilExpiry
            if (days != null && days in 0..30) {
                val title = "Document Expiring: ${doc.displayTitle}"
                val body = "Expires in $days days. Please renew ${doc.docNumber} before expiration."
                sendExpiryNotification(
                    context = context,
                    title = title,
                    body = body,
                    notificationId = 5000 + idx
                )
                notificationsSent++
            }
        }

        return notificationsSent
    }

    fun parseDayOfMonth(dateStr: String): Int? {
        if (dateStr.isBlank()) return null
        val trimmed = dateStr.trim()

        // 1. Try parsing full date formats first (e.g. 12/10/2026, 12-10-2026, 2026-10-12, 12 Oct 2026)
        val dateFormats = listOf(
            "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd",
            "dd MMM yyyy", "dd MMMM yyyy", "dd MMM", "dd MMMM"
        )
        for (format in dateFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = true
                val parsed = sdf.parse(trimmed)
                if (parsed != null) {
                    val cal = Calendar.getInstance().apply { time = parsed }
                    return cal.get(Calendar.DAY_OF_MONTH)
                }
            } catch (e: Exception) {
                // Try next
            }
        }

        // 2. Try regex extraction for ordinals or single numbers like "12th", "2nd", "12", "12th of month"
        val regex = Regex("""\b(\d{1,2})(?:st|nd|rd|th)?\b""", RegexOption.IGNORE_CASE)
        val match = regex.find(trimmed)
        if (match != null) {
            val num = match.groupValues[1].toIntOrNull()
            if (num != null && num in 1..31) {
                return num
            }
        }

        // 3. Fallback: filter digits and take first 2 digits
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length in 1..2) {
            return digits.toIntOrNull()?.takeIf { it in 1..31 }
        }

        return null
    }

    fun calculateDaysUntilDate(dateStr: String): Long? {
        val trimmed = dateStr.trim()
        val formats = listOf(
            "dd MMM yyyy", "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy",
            "MM/yy", "MM/yyyy", "dd MMM", "MMM yyyy", "dd/MM"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = true
                val parsed = sdf.parse(trimmed)
                if (parsed != null) {
                    val cal = Calendar.getInstance().apply { time = parsed }
                    // If format didn't include year (e.g. MM/yy with 2 digits, or dd MMM)
                    if (format == "MM/yy" || format == "MM/yyyy") {
                        // Set to last day of month
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                    val diff = cal.timeInMillis - System.currentTimeMillis()
                    return diff / (1000 * 60 * 60 * 24)
                }
            } catch (e: Exception) {
                // Try next format
            }
        }
        return null
    }

    fun scheduleDailyReminderAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReminderReceiver::class.java).apply {
            action = ACTION_REMINDER_CHECK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            777,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            // Handled
        }
    }
}

class AlarmReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NotificationHelper.createNotificationChannels(context)
                val db = com.example.data.local.AppDatabase.getDatabase(context)
                val cards = db.familyWalletDao().getAllCreditCardsList().map { it.toDomain() }
                val wallets = db.familyWalletDao().getAllWalletsAndGiftCardsList().map { it.toDomain() }
                val docs = db.familyWalletDao().getAllDocumentsList().map { it.toDomain() }
                NotificationHelper.checkAndTriggerDueReminders(context, cards, wallets, docs)
            } catch (e: Exception) {
                NotificationHelper.sendTestNotification(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
