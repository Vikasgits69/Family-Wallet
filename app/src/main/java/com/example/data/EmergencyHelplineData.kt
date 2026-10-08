package com.example.data

data class BankHelplineInfo(
    val bankName: String,
    val tollFreeNumber: String,
    val cardBlockNumber: String,
    val smsBlockFormat: String,
    val website: String,
    val popularNames: List<String>
)

object EmergencyHelplineData {
    val helplines = listOf(
        BankHelplineInfo(
            bankName = "State Bank of India (SBI)",
            tollFreeNumber = "18001234",
            cardBlockNumber = "18002100",
            smsBlockFormat = "BLOCK <last 4 digits> to 567676",
            website = "onlinesbi.sbi",
            popularNames = listOf("SBI", "State Bank", "State Bank of India")
        ),
        BankHelplineInfo(
            bankName = "HDFC Bank",
            tollFreeNumber = "18001641",
            cardBlockNumber = "18002026161",
            smsBlockFormat = "BLOCK <last 4 digits> to 5676712",
            website = "hdfcbank.com",
            popularNames = listOf("HDFC", "HDFC Bank")
        ),
        BankHelplineInfo(
            bankName = "ICICI Bank",
            tollFreeNumber = "18001080",
            cardBlockNumber = "18001038181",
            smsBlockFormat = "IBLOCK <last 4 digits> to 5676766",
            website = "icicibank.com",
            popularNames = listOf("ICICI", "ICICI Bank")
        ),
        BankHelplineInfo(
            bankName = "Axis Bank",
            tollFreeNumber = "18604195555",
            cardBlockNumber = "18605005555",
            smsBlockFormat = "BLOCKCARD <last 4 digits> to 5676782",
            website = "axisbank.com",
            popularNames = listOf("Axis", "Axis Bank")
        ),
        BankHelplineInfo(
            bankName = "Kotak Mahindra Bank",
            tollFreeNumber = "18602662666",
            cardBlockNumber = "18002090000",
            smsBlockFormat = "KBLOCK <last 4 digits> to 9971056767",
            website = "kotak.com",
            popularNames = listOf("Kotak", "Kotak Bank", "Kotak Mahindra")
        ),
        BankHelplineInfo(
            bankName = "Punjab National Bank (PNB)",
            tollFreeNumber = "18001802222",
            cardBlockNumber = "18001032222",
            smsBlockFormat = "HOTLIST <Card No> to 5607040",
            website = "pnbindia.in",
            popularNames = listOf("PNB", "Punjab National")
        ),
        BankHelplineInfo(
            bankName = "Bank of Baroda (BOB)",
            tollFreeNumber = "18005700",
            cardBlockNumber = "18002584455",
            smsBlockFormat = "BLOCK <last 4 digits> to 8422009988",
            website = "bankofbaroda.in",
            popularNames = listOf("BOB", "Bank of Baroda", "Baroda")
        ),
        BankHelplineInfo(
            bankName = "American Express (Amex)",
            tollFreeNumber = "18004192122",
            cardBlockNumber = "01242801111",
            smsBlockFormat = "Instant in Amex App or call helpline",
            website = "americanexpress.com/in",
            popularNames = listOf("Amex", "American Express")
        ),
        BankHelplineInfo(
            bankName = "IndusInd Bank",
            tollFreeNumber = "18602677777",
            cardBlockNumber = "18605005004",
            smsBlockFormat = "BLOCK <last 4 digits> to 9223512966",
            website = "indusind.com",
            popularNames = listOf("IndusInd", "IndusInd Bank")
        ),
        BankHelplineInfo(
            bankName = "Standard Chartered",
            tollFreeNumber = "18003451000",
            cardBlockNumber = "08042444444",
            smsBlockFormat = "BLOCK <last 4 digits> to 9987123123",
            website = "sc.com/in",
            popularNames = listOf("Standard Chartered", "StanChart")
        ),
        BankHelplineInfo(
            bankName = "RBL Bank",
            tollFreeNumber = "02261156300",
            cardBlockNumber = "18001238040",
            smsBlockFormat = "BLOCK <last 4 digits> to 9223366333",
            website = "rblbank.com",
            popularNames = listOf("RBL", "RBL Bank", "Ratnakar")
        ),
        BankHelplineInfo(
            bankName = "Yes Bank",
            tollFreeNumber = "18001200",
            cardBlockNumber = "18001035485",
            smsBlockFormat = "BLK <last 4 digits> to 9840909000",
            website = "yesbank.in",
            popularNames = listOf("Yes", "Yes Bank")
        )
    )

    fun findForBank(bankName: String): BankHelplineInfo? {
        val query = bankName.trim().lowercase()
        return helplines.firstOrNull { info ->
            info.popularNames.any { query.contains(it.lowercase()) } ||
            query.contains(info.bankName.lowercase())
        }
    }
}
