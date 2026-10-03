package com.example.data

object MockData {
    val familyMembers = listOf(
        FamilyMember(
            id = "mem_1",
            name = "Vikas Gupta",
            relationship = "Self (Primary)",
            avatarEmoji = "👑",
            colorHex = 0xFF6366F1,
            initials = "VG"
        ),
        FamilyMember(
            id = "mem_2",
            name = "Pooja Gupta",
            relationship = "Spouse",
            avatarEmoji = "✨",
            colorHex = 0xFFEC4899,
            initials = "PG"
        ),
        FamilyMember(
            id = "mem_3",
            name = "Ramesh Gupta",
            relationship = "Father",
            avatarEmoji = "👔",
            colorHex = 0xFF10B981,
            initials = "RG"
        ),
        FamilyMember(
            id = "mem_4",
            name = "Sunita Gupta",
            relationship = "Mother",
            avatarEmoji = "🌸",
            colorHex = 0xFFF59E0B,
            initials = "SG"
        ),
        FamilyMember(
            id = "mem_5",
            name = "Aarav Gupta",
            relationship = "Son",
            avatarEmoji = "🚀",
            colorHex = 0xFF3B82F6,
            initials = "AG"
        )
    )

    val creditCards = listOf(
        CreditCard(
            id = "cc_1",
            bankName = "HDFC Bank",
            cardName = "Tata Neu Infinity RuPay",
            network = CardNetwork.RUPAY,
            cardNumber = "4532890123457890",
            expiry = "08/29",
            cvv = "842",
            cardholderName = "VIKAS GUPTA",
            statementDate = "15th of every month",
            dueDate = "5th of every month",
            creditLimit = 400000.0,
            annualFee = 1499.0,
            waiverCondition = "Spend ₹3,00,000 annually to waive renewal fee",
            status = CardStatus.ACTIVE,
            memberId = "mem_1",
            themeColor = CardThemeColor.CHARCOAL
        ),
        CreditCard(
            id = "cc_2",
            bankName = "ICICI Bank",
            cardName = "Coral RuPay Credit Card",
            network = CardNetwork.RUPAY,
            cardNumber = "5241987654321098",
            expiry = "11/28",
            cvv = "319",
            cardholderName = "VIKAS GUPTA",
            statementDate = "20th of every month",
            dueDate = "10th of every month",
            creditLimit = 250000.0,
            annualFee = 500.0,
            waiverCondition = "Spend ₹1,50,000 annually to waive renewal fee",
            status = CardStatus.ACTIVE,
            memberId = "mem_1",
            themeColor = CardThemeColor.RUBY
        ),
        CreditCard(
            id = "cc_3",
            bankName = "State Bank of India",
            cardName = "SBI SimplySAVE RuPay",
            network = CardNetwork.RUPAY,
            cardNumber = "4111222233334444",
            expiry = "04/30",
            cvv = "671",
            cardholderName = "POOJA GUPTA",
            statementDate = "8th of every month",
            dueDate = "28th of every month",
            creditLimit = 180000.0,
            annualFee = 499.0,
            waiverCondition = "Spend ₹1,00,000 annually to waive fee",
            status = CardStatus.ACTIVE,
            memberId = "mem_2",
            themeColor = CardThemeColor.SAPPHIRE
        ),
        CreditCard(
            id = "cc_4",
            bankName = "Axis Bank",
            cardName = "Axis Bank Magnus VISA",
            network = CardNetwork.VISA,
            cardNumber = "4916890123456789",
            expiry = "01/27",
            cvv = "904",
            cardholderName = "VIKAS GUPTA",
            statementDate = "25th of every month",
            dueDate = "15th of every month",
            creditLimit = 750000.0,
            annualFee = 12500.0,
            waiverCondition = "Spend ₹15,00,000 annually for fee waiver",
            status = CardStatus.ACTIVE,
            memberId = "mem_1",
            themeColor = CardThemeColor.TITANIUM
        ),
        CreditCard(
            id = "cc_5",
            bankName = "American Express",
            cardName = "Amex Platinum Travel",
            network = CardNetwork.AMEX,
            cardNumber = "378282246310005",
            expiry = "06/28",
            cvv = "4512",
            cardholderName = "VIKAS GUPTA",
            statementDate = "3rd of every month",
            dueDate = "21st of every month",
            creditLimit = 500000.0,
            annualFee = 5000.0,
            waiverCondition = "Spend ₹4,00,000 annually for milestone reward vouchers",
            status = CardStatus.ACTIVE,
            memberId = "mem_1",
            themeColor = CardThemeColor.INDIGO
        )
    )

    val debitCards = listOf(
        DebitCard(
            id = "dc_1",
            bankName = "HDFC Bank",
            linkedAccount = "HDFC Priority Salary •••• 9812",
            network = CardNetwork.RUPAY,
            cardNumber = "6071829304152637",
            expiry = "12/29",
            cvv = "512",
            cardholderName = "VIKAS GUPTA",
            atmLimit = 100000.0,
            posLimit = 500000.0,
            status = CardStatus.ACTIVE,
            memberId = "mem_1",
            themeColor = CardThemeColor.EMERALD
        ),
        DebitCard(
            id = "dc_2",
            bankName = "State Bank of India",
            linkedAccount = "SBI Global Savings •••• 4431",
            network = CardNetwork.RUPAY,
            cardNumber = "6521998877665544",
            expiry = "03/31",
            cvv = "187",
            cardholderName = "RAMESH GUPTA",
            atmLimit = 40000.0,
            posLimit = 100000.0,
            status = CardStatus.ACTIVE,
            memberId = "mem_3",
            themeColor = CardThemeColor.SAPPHIRE
        ),
        DebitCard(
            id = "dc_3",
            bankName = "ICICI Bank",
            linkedAccount = "ICICI Privilege •••• 6610",
            network = CardNetwork.MASTERCARD,
            cardNumber = "5312789012345678",
            expiry = "09/27",
            cvv = "940",
            cardholderName = "POOJA GUPTA",
            atmLimit = 50000.0,
            posLimit = 200000.0,
            status = CardStatus.ACTIVE,
            memberId = "mem_2",
            themeColor = CardThemeColor.RUBY
        ),
        DebitCard(
            id = "dc_4",
            bankName = "Kotak Mahindra Bank",
            linkedAccount = "Kotak 811 Zero Balance •••• 1109",
            network = CardNetwork.RUPAY,
            cardNumber = "6080123498765432",
            expiry = "05/30",
            cvv = "336",
            cardholderName = "SUNITA GUPTA",
            atmLimit = 25000.0,
            posLimit = 50000.0,
            status = CardStatus.ACTIVE,
            memberId = "mem_4",
            themeColor = CardThemeColor.AMBER
        )
    )

    val onlineWallets = listOf(
        OnlineWallet(
            id = "wal_1",
            providerName = "Paytm Wallet",
            registeredMobile = "+91 98765 43210",
            registeredEmail = "vikas.gupta@example.com",
            upiId = "vikasgupta@paytm",
            kycStatus = KycStatus.FULL_KYC,
            walletLimit = 100000.0,
            status = WalletStatus.ACTIVE,
            memberId = "mem_1"
        ),
        OnlineWallet(
            id = "wal_2",
            providerName = "PhonePe",
            registeredMobile = "+91 98765 43210",
            registeredEmail = "vikas.gupta@example.com",
            upiId = "vikasgupta@ybl",
            kycStatus = KycStatus.FULL_KYC,
            walletLimit = 100000.0,
            status = WalletStatus.ACTIVE,
            memberId = "mem_1"
        ),
        OnlineWallet(
            id = "wal_3",
            providerName = "Google Pay (Tez)",
            registeredMobile = "+91 98765 43210",
            registeredEmail = "gupta.vikas45@gmail.com",
            upiId = "gupta.vikas45@okhdfcbank",
            kycStatus = KycStatus.FULL_KYC,
            walletLimit = 100000.0,
            status = WalletStatus.ACTIVE,
            memberId = "mem_1"
        ),
        OnlineWallet(
            id = "wal_4",
            providerName = "Amazon Pay",
            registeredMobile = "+91 98111 22334",
            registeredEmail = "pooja.g@example.com",
            upiId = "poojagupta@apl",
            kycStatus = KycStatus.MIN_KYC,
            walletLimit = 10000.0,
            status = WalletStatus.ACTIVE,
            memberId = "mem_2"
        ),
        OnlineWallet(
            id = "wal_5",
            providerName = "CRED UPI & Pay",
            registeredMobile = "+91 98765 43210",
            registeredEmail = "vikas.gupta@example.com",
            upiId = "vikas@axisbank",
            kycStatus = KycStatus.FULL_KYC,
            walletLimit = 200000.0,
            status = WalletStatus.ACTIVE,
            memberId = "mem_1"
        )
    )

    val bankAccounts = listOf(
        BankAccount(
            id = "acc_1",
            bankName = "HDFC Bank",
            accountType = AccountType.SALARY,
            accountNumber = "501002345678912",
            ifscCode = "HDFC0000240",
            branchName = "Connaught Place, New Delhi",
            accountHolderName = "VIKAS GUPTA",
            customerId = "84729103",
            linkedMobile = "+91 98765 43210",
            linkedUpi = "vikasgupta@hdfcbank",
            minBalance = 0.0,
            status = AccountStatus.ACTIVE,
            memberId = "mem_1"
        ),
        BankAccount(
            id = "acc_2",
            bankName = "State Bank of India",
            accountType = AccountType.SAVINGS,
            accountNumber = "309812345678",
            ifscCode = "SBIN0001234",
            branchName = "Main Branch, Sector 18, Noida",
            accountHolderName = "RAMESH GUPTA & VIKAS GUPTA",
            customerId = "73920194",
            linkedMobile = "+91 98765 43210",
            linkedUpi = "rameshgupta@sbi",
            minBalance = 3000.0,
            status = AccountStatus.ACTIVE,
            memberId = "mem_3"
        ),
        BankAccount(
            id = "acc_3",
            bankName = "ICICI Bank",
            accountType = AccountType.SAVINGS,
            accountNumber = "001101567890",
            ifscCode = "ICIC0000011",
            branchName = "Cyber City, Gurugram",
            accountHolderName = "POOJA GUPTA",
            customerId = "90218471",
            linkedMobile = "+91 98111 22334",
            linkedUpi = "pooja@icici",
            minBalance = 10000.0,
            status = AccountStatus.ACTIVE,
            memberId = "mem_2"
        ),
        BankAccount(
            id = "acc_4",
            bankName = "Kotak Mahindra Bank",
            accountType = AccountType.SAVINGS,
            accountNumber = "8110948372",
            ifscCode = "KKBK0000182",
            branchName = "Indirapuram, Ghaziabad",
            accountHolderName = "SUNITA GUPTA",
            customerId = "55492013",
            linkedMobile = "+91 98222 33445",
            linkedUpi = "sunitagupta@kotak",
            minBalance = 0.0,
            status = AccountStatus.ACTIVE,
            memberId = "mem_4"
        ),
        BankAccount(
            id = "acc_5",
            bankName = "Axis Bank",
            accountType = AccountType.FIXED_DEPOSIT,
            accountNumber = "920020084729103",
            ifscCode = "UTIB0000054",
            branchName = "Kasturba Gandhi Marg, Delhi",
            accountHolderName = "VIKAS GUPTA",
            customerId = "84729103",
            linkedMobile = "+91 98765 43210",
            linkedUpi = "vikas@axisbank",
            minBalance = 25000.0,
            status = AccountStatus.ACTIVE,
            memberId = "mem_1"
        )
    )
}
