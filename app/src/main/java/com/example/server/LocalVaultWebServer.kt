package com.example.server

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.data.AppThemeMode
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DocType
import com.example.data.Document
import com.example.data.FamilyMember
import com.example.data.WalletOrGiftCard
import com.example.ui.viewmodel.FamilyWalletUiState
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class LocalVaultWebServer(
    private val context: Context,
    val port: Int = 8080,
    var requirePin: Boolean = true,
    var sessionPin: String = "8492",
    private val dataProvider: () -> FamilyWalletUiState,
    private val onSaveCreditCard: (CreditCard) -> Unit,
    private val onDeleteCreditCard: (String) -> Unit,
    private val onSaveDebitCard: (DebitCard) -> Unit,
    private val onDeleteDebitCard: (String) -> Unit,
    private val onSaveBankAccount: (BankAccount) -> Unit,
    private val onDeleteBankAccount: (String) -> Unit,
    private val onSaveWalletOrGiftCard: (WalletOrGiftCard) -> Unit,
    private val onDeleteWalletOrGiftCard: (String) -> Unit,
    private val onSaveMember: (FamilyMember) -> Unit,
    private val onDeleteMember: (String) -> Unit,
    private val onSaveDocument: (Document) -> Unit,
    private val onDeleteDocument: (String) -> Unit,
    private val onSetThemeMode: (AppThemeMode) -> Unit = {},
    private val onSetCustomAccent: (String?) -> Unit = {},
    private val onLogActivity: (String) -> Unit = {}
) {

    private val isRunning = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null
    private val executor = Executors.newCachedThreadPool()
    val changeVersion = AtomicInteger(1)

    private val activeTokens = ConcurrentHashMap<String, Long>()
    private val clientIps = ConcurrentHashMap.newKeySet<String>()

    fun start(): Boolean {
        if (isRunning.get()) return true
        try {
            serverSocket = ServerSocket(port)
            isRunning.set(true)
            logActivity("Server started on port $port")

            executor.execute {
                while (isRunning.get() && serverSocket != null && !serverSocket!!.isClosed) {
                    try {
                        val clientSocket = serverSocket?.accept() ?: break
                        executor.execute {
                            handleClient(clientSocket)
                        }
                    } catch (e: Exception) {
                        if (isRunning.get()) {
                            Log.e("LocalVaultWebServer", "Error accepting connection", e)
                        }
                    }
                }
            }
            return true
        } catch (e: Exception) {
            Log.e("LocalVaultWebServer", "Failed to start server on port $port", e)
            logActivity("Failed to start server: ${e.message}")
            isRunning.set(false)
            return false
        }
    }

    fun stop() {
        isRunning.set(false)
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
        clientIps.clear()
        activeTokens.clear()
        logActivity("Server stopped")
    }

    fun notifyDataChanged() {
        changeVersion.incrementAndGet()
    }

    fun getConnectedClientCount(): Int = clientIps.size

    private fun logActivity(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        onLogActivity("[$time] $message")
    }

    private fun handleClient(socket: Socket) {
        val remoteIp = socket.inetAddress?.hostAddress ?: "Unknown"
        clientIps.add(remoteIp)

        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
            val output: OutputStream = socket.getOutputStream()

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return

            val method = parts[0].uppercase(Locale.ROOT)
            val fullPath = parts[1]
            val path = if (fullPath.contains("?")) fullPath.substringBefore("?") else fullPath
            val query = if (fullPath.contains("?")) fullPath.substringAfter("?") else ""

            val headers = mutableMapOf<String, String>()
            var line: String? = reader.readLine()
            while (!line.isNullOrBlank()) {
                val idx = line.indexOf(":")
                if (idx != -1) {
                    val key = line.substring(0, idx).trim().lowercase(Locale.ROOT)
                    val value = line.substring(idx + 1).trim()
                    headers[key] = value
                }
                line = reader.readLine()
            }

            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                val chars = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val read = reader.read(chars, readTotal, contentLength - readTotal)
                    if (read == -1) break
                    readTotal += read
                }
                String(chars, 0, readTotal)
            } else ""

            // Handle CORS preflight
            if (method == "OPTIONS") {
                sendResponse(output, 200, "text/plain", "")
                return
            }

            // Route Requests
            when {
                // Web Application UI
                (method == "GET" && (path == "/" || path == "/index.html")) -> {
                    val html = WebCompanionHtml.getHtml(context)
                    sendResponse(output, 200, "text/html; charset=utf-8", html)
                }

                // Server Status & Version
                (method == "GET" && path == "/api/status") -> {
                    val json = JSONObject().apply {
                        put("status", "online")
                        put("requirePin", requirePin)
                        put("version", changeVersion.get())
                        put("deviceName", "${Build.MANUFACTURER} ${Build.MODEL}")
                        put("port", port)
                    }
                    sendResponse(output, 200, "application/json; charset=utf-8", json.toString())
                }

                // PIN Authorization
                (method == "POST" && path == "/api/auth") -> {
                    val reqJson = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                    val submittedPin = reqJson.optString("pin", "")
                    if (!requirePin || submittedPin == sessionPin) {
                        val token = UUID.randomUUID().toString()
                        activeTokens[token] = System.currentTimeMillis()
                        logActivity("Web client authenticated successfully ($remoteIp)")
                        val res = JSONObject().apply {
                            put("success", true)
                            put("token", token)
                        }
                        sendResponse(output, 200, "application/json; charset=utf-8", res.toString())
                    } else {
                        logActivity("Failed authentication attempt ($remoteIp)")
                        val res = JSONObject().apply {
                            put("success", false)
                            put("error", "Invalid PIN")
                        }
                        sendResponse(output, 401, "application/json; charset=utf-8", res.toString())
                    }
                }

                // Full Vault Data Export for Browser
                (method == "GET" && path == "/api/vault") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }

                    val state = dataProvider()
                    val json = serializeVaultState(state)
                    sendResponse(output, 200, "application/json; charset=utf-8", json.toString())
                }

                // CRUD: Credit Cards
                (method == "POST" && path == "/api/cards/credit") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val card = parseCreditCard(json)
                    onSaveCreditCard(card)
                    changeVersion.incrementAndGet()
                    logActivity("Saved Credit Card: ${card.bankName} ${card.cardName}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/cards/credit") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteCreditCard(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Credit Card ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // CRUD: Debit Cards
                (method == "POST" && path == "/api/cards/debit") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val card = parseDebitCard(json)
                    onSaveDebitCard(card)
                    changeVersion.incrementAndGet()
                    logActivity("Saved Debit Card: ${card.bankName} ${card.cardName}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/cards/debit") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteDebitCard(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Debit Card ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // CRUD: Bank Accounts
                (method == "POST" && path == "/api/banks") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val bank = parseBankAccount(json)
                    onSaveBankAccount(bank)
                    changeVersion.incrementAndGet()
                    logActivity("Saved Bank Account: ${bank.bankName}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/banks") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteBankAccount(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Bank Account ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // CRUD: Wallets & Gift Cards
                (method == "POST" && path == "/api/wallets") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val wallet = parseWalletOrGiftCard(json)
                    onSaveWalletOrGiftCard(wallet)
                    changeVersion.incrementAndGet()
                    logActivity("Saved ${if (wallet.isGiftCard) "Gift Card" else "Wallet"}: ${wallet.providerOrName}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/wallets") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteWalletOrGiftCard(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Wallet/Gift Card ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // CRUD: Family Members
                (method == "POST" && path == "/api/members") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val member = parseFamilyMember(json)
                    onSaveMember(member)
                    changeVersion.incrementAndGet()
                    logActivity("Saved Family Member: ${member.name}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/members") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteMember(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Member ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // CRUD: Documents
                (method == "POST" && path == "/api/documents") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val json = JSONObject(body)
                    val doc = parseDocument(json)
                    onSaveDocument(doc)
                    changeVersion.incrementAndGet()
                    logActivity("Saved Document: ${doc.title}")
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                (method == "DELETE" && path == "/api/documents") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val id = getQueryParam(query, "id")
                    if (id.isNotBlank()) {
                        onDeleteDocument(id)
                        changeVersion.incrementAndGet()
                        logActivity("Deleted Document ID: $id")
                        sendResponse(output, 200, "application/json", "{\"success\":true}")
                    } else {
                        sendResponse(output, 400, "application/json", "{\"error\":\"Missing id parameter\"}")
                    }
                }

                // Theme Configuration (Get & Set)
                (method == "GET" && path == "/api/theme") -> {
                    val state = dataProvider()
                    val res = JSONObject().apply {
                        put("themeMode", state.themeMode.name)
                        put("customAccent", state.customAccentColorHex ?: "")
                        put("isDarkTheme", state.isDarkTheme)
                        val themeList = JSONArray()
                        AppThemeMode.values().forEach { t ->
                            themeList.put(JSONObject().apply {
                                put("name", t.name)
                                put("title", t.title)
                                put("subtitle", t.subtitle)
                                put("emoji", t.emoji)
                                put("category", t.category.name)
                                put("categoryTitle", t.category.label)
                                put("isDark", t.isDark)
                            })
                        }
                        put("availableThemes", themeList)
                    }
                    sendResponse(output, 200, "application/json; charset=utf-8", res.toString())
                }

                (method == "POST" && path == "/api/theme") -> {
                    if (!checkAuth(headers)) {
                        sendResponse(output, 401, "application/json", "{\"error\":\"Unauthorized\"}")
                        return
                    }
                    val reqJson = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                    val modeStr = reqJson.optString("themeMode", "")
                    if (modeStr.isNotBlank()) {
                        try {
                            val mode = AppThemeMode.valueOf(modeStr)
                            onSetThemeMode(mode)
                            logActivity("Theme updated from web: ${mode.title}")
                        } catch (e: Exception) {
                            Log.e("LocalVaultWebServer", "Invalid theme: $modeStr", e)
                        }
                    }
                    if (reqJson.has("customAccent")) {
                        val accent = reqJson.optString("customAccent").takeIf { it.isNotBlank() }
                        onSetCustomAccent(accent)
                    }
                    changeVersion.incrementAndGet()
                    sendResponse(output, 200, "application/json", "{\"success\":true}")
                }

                else -> {
                    sendResponse(output, 404, "text/plain", "Not Found")
                }
            }
        } catch (e: Exception) {
            Log.e("LocalVaultWebServer", "Error processing request", e)
        } finally {
            try {
                socket.close()
            } catch (_: Exception) {}
        }
    }

    private fun checkAuth(headers: Map<String, String>): Boolean {
        if (!requirePin) return true
        val token = headers["authorization"] ?: ""
        return token.isNotBlank() && activeTokens.containsKey(token)
    }

    private fun getQueryParam(query: String, param: String): String {
        val pairs = query.split("&")
        for (pair in pairs) {
            val parts = pair.split("=")
            if (parts.size == 2 && parts[0] == param) {
                return URLDecoder.decode(parts[1], "UTF-8")
            }
        }
        return ""
    }

    private fun sendResponse(output: OutputStream, statusCode: Int, contentType: String, body: String) {
        val statusText = when (statusCode) {
            200 -> "OK"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            404 -> "Not Found"
            else -> "Error"
        }
        val bytes = body.toByteArray(Charsets.UTF_8)
        val header = "HTTP/1.1 $statusCode $statusText\r\n" +
                "Content-Type: $contentType\r\n" +
                "Content-Length: ${bytes.size}\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With\r\n" +
                "Connection: close\r\n\r\n"

        output.write(header.toByteArray(Charsets.UTF_8))
        output.write(bytes)
        output.flush()
    }

    // JSON Serializers
    private fun serializeVaultState(state: FamilyWalletUiState): JSONObject {
        return JSONObject().apply {
            put("version", changeVersion.get())
            put("themeMode", state.themeMode.name)
            put("customAccent", state.customAccentColorHex ?: "")
            put("isDarkTheme", state.isDarkTheme)
            val themeList = JSONArray()
            AppThemeMode.values().forEach { t ->
                themeList.put(JSONObject().apply {
                    put("name", t.name)
                    put("title", t.title)
                    put("subtitle", t.subtitle)
                    put("emoji", t.emoji)
                    put("category", t.category.name)
                    put("categoryTitle", t.category.label)
                    put("isDark", t.isDark)
                })
            }
            put("availableThemes", themeList)

            val membersArray = JSONArray()
            state.members.forEach { m ->
                membersArray.put(JSONObject().apply {
                    put("id", m.id)
                    put("name", m.name)
                    put("relationship", m.relationship)
                    put("relationshipCategory", m.relationshipCategory)
                    put("bloodGroup", m.bloodGroup)
                    put("emergencyPhone", m.emergencyPhone)
                    put("isEmergencyContact", m.isEmergencyContact)
                    put("initials", m.initials)
                })
            }
            put("members", membersArray)

            val ccArray = JSONArray()
            state.creditCards.forEach { c ->
                ccArray.put(JSONObject().apply {
                    put("id", c.id)
                    put("bankName", c.bankName)
                    put("cardName", c.cardName)
                    put("cardholderName", c.cardholderName)
                    put("network", c.network.name)
                    put("cardNumber", c.cardNumber)
                    put("expiry", c.expiry)
                    put("cvv", c.cvv)
                    put("statementDate", c.statementDate)
                    put("dueDate", c.dueDate)
                    put("memberId", c.memberId)
                    put("colorHex", c.colorHex)
                })
            }
            put("creditCards", ccArray)

            val dcArray = JSONArray()
            state.debitCards.forEach { c ->
                dcArray.put(JSONObject().apply {
                    put("id", c.id)
                    put("bankName", c.bankName)
                    put("cardName", c.cardName)
                    put("cardholderName", c.cardholderName)
                    put("network", c.network.name)
                    put("cardNumber", c.cardNumber)
                    put("expiry", c.expiry)
                    put("cvv", c.cvv)
                    put("memberId", c.memberId)
                    put("colorHex", c.colorHex)
                })
            }
            put("debitCards", dcArray)

            val bankArray = JSONArray()
            state.bankAccounts.forEach { b ->
                bankArray.put(JSONObject().apply {
                    put("id", b.id)
                    put("bankName", b.bankName)
                    put("accountType", b.accountType)
                    put("accountHolderName", b.accountHolderName)
                    put("accountNumber", b.accountNumber)
                    put("ifscCode", b.ifscCode)
                    put("branchName", b.branchName)
                    put("memberId", b.memberId)
                    put("colorHex", b.colorHex)
                })
            }
            put("bankAccounts", bankArray)

            val walletArray = JSONArray()
            state.walletsAndGiftCards.forEach { w ->
                walletArray.put(JSONObject().apply {
                    put("id", w.id)
                    put("isGiftCard", w.isGiftCard)
                    put("providerOrName", w.providerOrName)
                    put("cardNumberOrUpi", w.cardNumberOrUpi)
                    put("giftCardPin", w.giftCardPin)
                    put("amount", w.amount)
                    put("currentBalance", w.currentBalance)
                    put("expiryDate", w.expiryDate)
                    put("memberId", w.memberId)
                    put("colorHex", w.colorHex)
                })
            }
            put("walletsAndGiftCards", walletArray)

            val docArray = JSONArray()
            state.documents.forEach { d ->
                docArray.put(JSONObject().apply {
                    put("id", d.id)
                    put("title", d.title)
                    put("docNumber", d.docNumber)
                    put("notes", d.notes ?: "")
                    put("memberId", d.memberId)
                    put("colorHex", d.colorHex)
                })
            }
            put("documents", docArray)
        }
    }

    // Parsers from JSON to Domain Models
    private fun parseCreditCard(json: JSONObject): CreditCard {
        val netStr = json.optString("network", "VISA")
        val network = try { CardNetwork.valueOf(netStr) } catch (_: Exception) { CardNetwork.VISA }
        return CreditCard(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            bankName = json.optString("bankName", "Bank"),
            cardName = json.optString("cardName", "Credit Card"),
            network = network,
            cardNumber = json.optString("cardNumber", ""),
            expiry = json.optString("expiry", ""),
            cvv = json.optString("cvv", ""),
            cardholderName = json.optString("cardholderName", ""),
            statementDate = json.optString("statementDate", ""),
            dueDate = json.optString("dueDate", ""),
            memberId = json.optString("memberId", ""),
            status = CardStatus.ACTIVE
        )
    }

    private fun parseDebitCard(json: JSONObject): DebitCard {
        val netStr = json.optString("network", "VISA")
        val network = try { CardNetwork.valueOf(netStr) } catch (_: Exception) { CardNetwork.VISA }
        return DebitCard(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            bankName = json.optString("bankName", "Bank"),
            cardName = json.optString("cardName", "Debit Card"),
            network = network,
            cardNumber = json.optString("cardNumber", ""),
            expiry = json.optString("expiry", ""),
            cvv = json.optString("cvv", ""),
            cardholderName = json.optString("cardholderName", ""),
            memberId = json.optString("memberId", ""),
            status = CardStatus.ACTIVE
        )
    }

    private fun parseBankAccount(json: JSONObject): BankAccount {
        return BankAccount(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            bankName = json.optString("bankName", "Bank"),
            accountType = json.optString("accountType", "Savings"),
            accountNumber = json.optString("accountNumber", ""),
            ifscCode = json.optString("ifscCode", "").uppercase(Locale.ROOT),
            accountHolderName = json.optString("accountHolderName", ""),
            branchName = json.optString("branchName", ""),
            memberId = json.optString("memberId", "")
        )
    }

    private fun parseWalletOrGiftCard(json: JSONObject): WalletOrGiftCard {
        val isGiftCard = json.optBoolean("isGiftCard", false)
        val amt = json.optDouble("amount", 0.0)
        return WalletOrGiftCard(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            isGiftCard = isGiftCard,
            providerOrName = json.optString("providerOrName", if (isGiftCard) "Gift Card" else "Wallet"),
            cardNumberOrUpi = json.optString("cardNumberOrUpi", ""),
            giftCardPin = json.optString("giftCardPin", ""),
            amount = amt,
            currentBalance = amt,
            expiryDate = json.optString("expiryDate", ""),
            memberId = json.optString("memberId", "")
        )
    }

    private fun parseFamilyMember(json: JSONObject): FamilyMember {
        return FamilyMember(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            name = json.optString("name", "Family Member"),
            relationship = json.optString("relationship", "Other"),
            relationshipCategory = json.optString("relationshipCategory", "OTHERS"),
            bloodGroup = json.optString("bloodGroup", ""),
            emergencyPhone = json.optString("emergencyPhone", ""),
            isEmergencyContact = json.optBoolean("isEmergencyContact", false)
        )
    }

    private fun parseDocument(json: JSONObject): Document {
        return Document(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            title = json.optString("title", "Document"),
            docType = DocType.OTHER,
            docNumber = json.optString("docNumber", ""),
            notes = json.optString("notes", ""),
            memberId = json.optString("memberId", "")
        )
    }
}
