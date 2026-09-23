package com.frogobox.libkeyboard.ui.emoji

/**
 * High-speed bilingual (Indonesian & English) keyword search engine for emojis.
 */
object EmojiSearchEngine {

    private data class EmojiEntry(
        val emoji: String,
        val keywords: List<String>
    )

    private val emojiKeywordDatabase = listOf(
        // Smileys & Emotion
        EmojiEntry("😀", listOf("smile", "grinning", "senyum", "happy", "senang", "gembira")),
        EmojiEntry("😃", listOf("smile", "happy", "senang", "gembira", "ceria")),
        EmojiEntry("😄", listOf("smile", "laugh", "senyum", "tertawa", "senang")),
        EmojiEntry("😁", listOf("grin", "gigi", "senyum", "smile", "happy")),
        EmojiEntry("😆", listOf("laugh", "tertawa", "ngakak", "gembira")),
        EmojiEntry("😅", listOf("sweat", "smile", "keringat", "lega", "relief")),
        EmojiEntry("🤣", listOf("rofl", "rolling", "laugh", "ngakak", "guling", "tertawa", "lucu", "funny")),
        EmojiEntry("😂", listOf("joy", "tears", "laugh", "tertawa", "nangis", "ngakak", "lucu", "funny")),
        EmojiEntry("🙂", listOf("slight", "smile", "senyum", "halus", "ramah")),
        EmojiEntry("🙃", listOf("upside", "down", "terbalik", "ironi", "sarcasm")),
        EmojiEntry("😉", listOf("wink", "kedip", "genit")),
        EmojiEntry("😊", listOf("blush", "smile", "senyum", "malu", "ramah", "happy")),
        EmojiEntry("😇", listOf("angel", "halo", "malaikat", "baik", "innocent")),
        EmojiEntry("🥰", listOf("love", "adore", "cinta", "sayang", "suka", "hearts")),
        EmojiEntry("😍", listOf("heart", "eyes", "love", "cinta", "suka", "naksir", "kagum")),
        EmojiEntry("🤩", listOf("star", "struck", "bintang", "kagum", "terpukau")),
        EmojiEntry("😘", listOf("kiss", "love", "cium", "cinta", "sayang")),
        EmojiEntry("😗", listOf("kiss", "cium", "siul")),
        EmojiEntry("😚", listOf("kiss", "cium", "sayang")),
        EmojiEntry("😙", listOf("kiss", "cium")),
        EmojiEntry("🥲", listOf("tear", "smile", "haru", "senang", "terharu")),
        EmojiEntry("😋", listOf("yum", "delicious", "enak", "lezat", "lapar")),
        EmojiEntry("😛", listOf("tongue", "lidah", "canda")),
        EmojiEntry("😜", listOf("wink", "tongue", "kedip", "lidah", "iseng")),
        EmojiEntry("🤪", listOf("zany", "crazy", "gila", "lucu", "konyol")),
        EmojiEntry("😝", listOf("tongue", "lidah", "squint")),
        EmojiEntry("🤑", listOf("money", "uang", "kaya", "cuan", "rich", "dollar", "rupiah")),
        EmojiEntry("🤗", listOf("hug", "peluk", "hangat", "ramah")),
        EmojiEntry("🤭", listOf("giggle", "oops", "tutup mulut", "malu")),
        EmojiEntry("🤫", listOf("shh", "quiet", "diam", "rahasia", "secret")),
        EmojiEntry("🤔", listOf("think", "mikir", "bingung", "ragu", "ponder")),
        EmojiEntry("🫡", listOf("salute", "hormat", "siap", "laksanakan")),
        EmojiEntry("🤐", listOf("zipper", "bungkam", "diam")),
        EmojiEntry("🤨", listOf("eyebrow", "curiga", "ragu", "skeptical")),
        EmojiEntry("😐", listOf("neutral", "datar", "biasa")),
        EmojiEntry("😑", listOf("expressionless", "datar", "lelah")),
        EmojiEntry("😶", listOf("speechless", "terdiam", "bisu")),
        EmojiEntry("😏", listOf("smirk", "senyum licik", "sombong")),
        EmojiEntry("😒", listOf("unamused", "bosan", "kesal", "malas")),
        EmojiEntry("🙄", listOf("roll eyes", "putar mata", "malas", "heran")),
        EmojiEntry("😬", listOf("grimace", "meringis", "canggung")),
        EmojiEntry("🤥", listOf("liar", "bohong", "pinocchio")),
        EmojiEntry("😌", listOf("relieved", "lega", "tenang", "syukur")),
        EmojiEntry("😔", listOf("pensive", "sedih", "murung", "melamun")),
        EmojiEntry("😪", listOf("sleepy", "ngantuk", "tidur")),
        EmojiEntry("🤤", listOf("drool", "ngiler", "enak", "mau")),
        EmojiEntry("😴", listOf("sleeping", "tidur", "ngantuk", "lelah")),
        EmojiEntry("😷", listOf("mask", "masker", "sakit", "covid")),
        EmojiEntry("🤒", listOf("fever", "demam", "sakit", "termometer")),
        EmojiEntry("🤕", listOf("hurt", "luka", "perban", "sakit")),
        EmojiEntry("🤢", listOf("nauseated", "mual", "jijik")),
        EmojiEntry("🤮", listOf("vomit", "muntah", "jijik")),
        EmojiEntry("🤧", listOf("sneeze", "bersin", "flu", "pilek")),
        EmojiEntry("🥵", listOf("hot", "panas", "kegerahan")),
        EmojiEntry("🥶", listOf("cold", "dingin", "beku")),
        EmojiEntry("🥴", listOf("woozy", "mabuk", "pusing")),
        EmojiEntry("😵", listOf("dizzy", "pusing", "pingsan")),
        EmojiEntry("🤯", listOf("mind blown", "kaget", "meledak", "shock")),
        EmojiEntry("🤠", listOf("cowboy", "topi", "koboi")),
        EmojiEntry("🥳", listOf("party", "pesta", "selamat", "congrats", "ultah", "birthday")),
        EmojiEntry("😎", listOf("cool", "sunglasses", "keren", "kacamata", "kece", "mantap")),
        EmojiEntry("🤓", listOf("nerd", "kacamata", "pintar", "belajar")),
        EmojiEntry("🧐", listOf("monocle", "kepo", "selidiki", "periksa")),
        EmojiEntry("😕", listOf("confused", "bingung", "ragu")),
        EmojiEntry("😟", listOf("worried", "khawatir", "cemas")),
        EmojiEntry("🙁", listOf("frown", "cemberut", "sedih")),
        EmojiEntry("😮", listOf("surprise", "kaget", "terkejut")),
        EmojiEntry("😯", listOf("hushed", "kaget")),
        EmojiEntry("😲", listOf("astonished", "tercengang", "kagum")),
        EmojiEntry("😳", listOf("flushed", "kaget", "malu")),
        EmojiEntry("🥺", listOf("pleading", "mohon", "tolong", "sedih", "puppy eyes")),
        EmojiEntry("😦", listOf("frown", "kaget")),
        EmojiEntry("😨", listOf("fear", "takut", "ngeri")),
        EmojiEntry("😰", listOf("anxious", "cemas", "keringat")),
        EmojiEntry("😥", listOf("sad", "sedih", "kecewa")),
        EmojiEntry("😢", listOf("crying", "nangis", "sedih", "air mata")),
        EmojiEntry("😭", listOf("loud cry", "nangis keras", "sedih banget", "kejer")),
        EmojiEntry("😱", listOf("screaming", "teriak", "kaget", "takut", "horror")),
        EmojiEntry("😖", listOf("confounded", "kesal", "frustasi")),
        EmojiEntry("😣", listOf("persevering", "tahan", "sakit")),
        EmojiEntry("😞", listOf("disappointed", "kecewa", "sedih", "patah hati")),
        EmojiEntry("😓", listOf("downcast", "keringat", "kecewa")),
        EmojiEntry("😩", listOf("weary", "lelah", "capek", "cape")),
        EmojiEntry("😫", listOf("tired", "capek", "lelah", "cape")),
        EmojiEntry("🥱", listOf("yawn", "menguap", "ngantuk", "bosan")),
        EmojiEntry("😤", listOf("triumph", "kesal", "marah", "bangga")),
        EmojiEntry("😡", listOf("angry", "marah", "kesal", "ngamuk")),
        EmojiEntry("😠", listOf("angry", "marah", "cemberut")),
        EmojiEntry("🤬", listOf("cursing", "marah", "sumpah serapah", "ngamuk")),
        EmojiEntry("😈", listOf("devil", "setan", "iblis", "jahil")),
        EmojiEntry("👿", listOf("demon", "iblis", "jahat")),
        EmojiEntry("💀", listOf("skull", "tengkorak", "mati", "dead")),
        EmojiEntry("💩", listOf("poop", "tinja", "lucu")),
        EmojiEntry("🤡", listOf("clown", "badut", "lucu")),
        EmojiEntry("👻", listOf("ghost", "hantu", "seram")),
        EmojiEntry("👽", listOf("alien", "luar angkasa")),
        EmojiEntry("🤖", listOf("robot", "bot", "mesin")),

        // Hands & Gestures
        EmojiEntry("👋", listOf("wave", "hai", "halo", "dadah", "bye", "salam")),
        EmojiEntry("🤚", listOf("raised back of hand", "tangan", "stop")),
        EmojiEntry("🖐️", listOf("hand with fingers", "tangan", "lima")),
        EmojiEntry("✋", listOf("raised hand", "tangan", "stop", "tunggu")),
        EmojiEntry("🖖", listOf("vulcan", "spock", "damai")),
        EmojiEntry("👌", listOf("ok", "oke", "siap", "mantap", "setuju")),
        EmojiEntry("🤌", listOf("pinched fingers", "italia", "maksud")),
        EmojiEntry("🤏", listOf("pinching", "sedikit", "dikit")),
        EmojiEntry("✌️", listOf("peace", "damai", "dua", "victory")),
        EmojiEntry("🤞", listOf("crossed fingers", "harapan", "semoga", "luck")),
        EmojiEntry("🤟", listOf("love you", "cinta", "metal")),
        EmojiEntry("🤘", listOf("rock", "metal", "keren")),
        EmojiEntry("🤙", listOf("call", "telepon", "santai", "hang loose")),
        EmojiEntry("👈", listOf("left", "kiri", "tunjuk")),
        EmojiEntry("👉", listOf("right", "kanan", "tunjuk")),
        EmojiEntry("👆", listOf("up", "atas", "tunjuk")),
        EmojiEntry("👇", listOf("down", "bawah", "tunjuk")),
        EmojiEntry("☝️", listOf("point up", "satu", "atas", "ingat")),
        EmojiEntry("👍", listOf("thumbs up", "jempol", "bagus", "mantap", "oke", "setuju", "like")),
        EmojiEntry("👎", listOf("thumbs down", "jelek", "buruk", "tidak suka", "dislike")),
        EmojiEntry("✊", listOf("fist", "tinju", "semangat", "perjuangan")),
        EmojiEntry("👊", listOf("punch", "tinju", "tos", "bro")),
        EmojiEntry("🤛", listOf("left fist", "tos")),
        EmojiEntry("🤜", listOf("right fist", "tos")),
        EmojiEntry("👏", listOf("clap", "tepuk tangan", "selamat", "applause", "hebat")),
        EmojiEntry("🙌", listOf("raising hands", "hore", "syukur", "sukses")),
        EmojiEntry("👐", listOf("open hands", "terbuka", "tangan")),
        EmojiEntry("🤲", listOf("palms up", "doa", "memohon", "tadah")),
        EmojiEntry("🤝", listOf("handshake", "jabat tangan", "deal", "sepakat", "setuju", "kerjasama")),
        EmojiEntry("🙏", listOf("pray", "doa", "tolong", "terima kasih", "thanks", "makasih", "mohon", "maaf", "sungkem")),
        EmojiEntry("✍️", listOf("writing", "tulis", "catat")),
        EmojiEntry("💅", listOf("nail polish", "kuku", "santai")),
        EmojiEntry("🤳", listOf("selfie", "foto", "hp")),
        EmojiEntry("💪", listOf("muscle", "strong", "otot", "kuat", "semangat")),

        // Hearts & Symbols
        EmojiEntry("❤️", listOf("heart", "red heart", "cinta", "hati", "sayang", "merah")),
        EmojiEntry("🧡", listOf("orange heart", "hati oranye")),
        EmojiEntry("💛", listOf("yellow heart", "hati kuning")),
        EmojiEntry("💚", listOf("green heart", "hati hijau")),
        EmojiEntry("💙", listOf("blue heart", "hati biru")),
        EmojiEntry("💜", listOf("purple heart", "hati ungu")),
        EmojiEntry("🖤", listOf("black heart", "hati hitam")),
        EmojiEntry("🤍", listOf("white heart", "hati putih")),
        EmojiEntry("🤎", listOf("brown heart", "hati cokelat")),
        EmojiEntry("💔", listOf("broken heart", "patah hati", "kecewa", "sedih")),
        EmojiEntry("❣️", listOf("heart exclamation", "tanda seru hati")),
        EmojiEntry("💕", listOf("two hearts", "dua hati", "cinta", "sayang")),
        EmojiEntry("💞", listOf("revolving hearts", "hati berputar")),
        EmojiEntry("💓", listOf("beating heart", "detak jantung", "deg-degan")),
        EmojiEntry("💗", listOf("growing heart", "hati berkembang")),
        EmojiEntry("💖", listOf("sparkling heart", "hati berkilau", "cinta")),
        EmojiEntry("💘", listOf("heart with arrow", "panah asmara", "naksir")),
        EmojiEntry("💝", listOf("heart with ribbon", "kado", "hadiah hati")),
        EmojiEntry("🔥", listOf("fire", "api", "hot", "panas", "semangat", "viral", "trending")),
        EmojiEntry("⭐", listOf("star", "bintang", "review", "rating", "favorit", "bagus")),
        EmojiEntry("🌟", listOf("glowing star", "bintang bersinar", "keren")),
        EmojiEntry("✨", listOf("sparkles", "kilau", "bersih", "kinclong", "magic")),
        EmojiEntry("⚡", listOf("lightning", "kilat", "petir", "cepat", "fast")),
        EmojiEntry("💥", listOf("collision", "ledakan", "boom")),
        EmojiEntry("💯", listOf("hundred", "seratus", "sempurna", "perfect", "jos", "mantap")),
        EmojiEntry("✅", listOf("check", "centang", "benar", "sukses", "selesai", "lunas", "yes")),
        EmojiEntry("✔️", listOf("check mark", "centang")),
        EmojiEntry("❌", listOf("cross", "silang", "salah", "batal", "cancel", "no")),
        EmojiEntry("🚫", listOf("prohibited", "larangan", "dilarang", "stop")),
        EmojiEntry("⚠️", listOf("warning", "peringatan", "awas", "hati-hati")),
        EmojiEntry("⛔", listOf("no entry", "stop", "dilarang")),
        EmojiEntry("❓", listOf("question", "tanya", "pertanyaan")),
        EmojiEntry("❗", listOf("exclamation", "seru", "perhatian", "penting")),

        // Seller & Commerce (E-Commerce Highlights)
        EmojiEntry("💰", listOf("money bag", "uang", "kantong uang", "dana", "cuan", "gaji", "bayar")),
        EmojiEntry("💵", listOf("dollar", "uang", "tunai", "cash", "rupiah")),
        EmojiEntry("💸", listOf("money with wings", "uang terbang", "boros", "pengeluaran")),
        EmojiEntry("💳", listOf("credit card", "kartu kredit", "atm", "debit", "rekening", "transfer")),
        EmojiEntry("🪙", listOf("coin", "koin", "uang")),
        EmojiEntry("📦", listOf("package", "paket", "kardus", "barang", "kirim", "shipping", "resi", "ongkir", "box")),
        EmojiEntry("🚚", listOf("truck", "truk", "pengiriman", "ekspedisi", "kurir", "kirim")),
        EmojiEntry("🚛", listOf("articulated lorry", "truk besar", "kontainer")),
        EmojiEntry("🛵", listOf("motor scooter", "motor", "kurir", "ojol", "antar")),
        EmojiEntry("🚲", listOf("bicycle", "sepeda")),
        EmojiEntry("🛒", listOf("shopping cart", "keranjang", "troli", "belanja", "beli")),
        EmojiEntry("🛍️", listOf("shopping bags", "tas belanja", "toko", "olshop", "belanja")),
        EmojiEntry("🏷️", listOf("label", "tag", "harga", "diskon", "promo")),
        EmojiEntry("🎁", listOf("gift", "kado", "hadiah", "bonus", "free")),
        EmojiEntry("🎉", listOf("party popper", "selamat", "congrats", "pesta", "hore")),
        EmojiEntry("🎊", listOf("confetti ball", "selamat", "meriah")),
        EmojiEntry("🏆", listOf("trophy", "piala", "juara", "terbaik", "bestseller")),
        EmojiEntry("🥇", listOf("1st place medal", "juara satu", "emas", "top")),
        EmojiEntry("📱", listOf("mobile phone", "hp", "handphone", "smartphone", "wa", "telepon")),
        EmojiEntry("💻", listOf("laptop", "komputer", "pc", "admin")),
        EmojiEntry("📞", listOf("telephone receiver", "telepon", "hubungi")),
        EmojiEntry("💬", listOf("speech balloon", "chat", "pesan", "inbox", "balas")),
        EmojiEntry("🧾", listOf("receipt", "struk", "invoice", "nota", "bukti bayar")),
        EmojiEntry("🔒", listOf("locked", "aman", "secure", "gembok", "terkunci")),
        EmojiEntry("🔓", listOf("unlocked", "terbuka")),

        // Food & Drink
        EmojiEntry("☕", listOf("coffee", "kopi", "panas", "cafe", "santai")),
        EmojiEntry("🍵", listOf("tea", "teh", "minum")),
        EmojiEntry("🧋", listOf("boba", "boba tea", "minuman")),
        EmojiEntry("🥤", listOf("cup with straw", "minuman", "es")),
        EmojiEntry("🍚", listOf("rice", "nasi", "makan")),
        EmojiEntry("🍜", listOf("noodles", "mie", "bakso", "ramen")),
        EmojiEntry("🍕", listOf("pizza", "makanan", "enak")),
        EmojiEntry("🍔", listOf("burger", "fast food")),
        EmojiEntry("🍗", listOf("chicken", "ayam", "makan")),
        EmojiEntry("🍞", listOf("bread", "roti")),
        EmojiEntry("🎂", listOf("birthday cake", "kue", "ultah")),
        EmojiEntry("🍦", listOf("ice cream", "es krim", "manis")),

        // Animals & Nature
        EmojiEntry("🐱", listOf("cat", "kucing", "lucu", "meow")),
        EmojiEntry("🐶", listOf("dog", "anjing", "lucu", "guguk")),
        EmojiEntry("🐸", listOf("frog", "katak", "kodok", "frogo")),
        EmojiEntry("🐵", listOf("monkey", "monyet")),
        EmojiEntry("🦁", listOf("lion", "singa")),
        EmojiEntry("🐯", listOf("tiger", "harimau")),
        EmojiEntry("🌸", listOf("cherry blossom", "bunga", "cantik")),
        EmojiEntry("🌹", listOf("rose", "mawar", "bunga", "cinta")),
        EmojiEntry("🌻", listOf("sunflower", "bunga matahari"))
    )

    /**
     * Searches the emoji database for the given [query] across English and Indonesian keywords.
     * Returns matching emoji characters ordered by relevance.
     */
    fun search(query: String): List<String> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return emptyList()

        val results = mutableListOf<String>()
        val exactMatches = mutableListOf<String>()
        val prefixMatches = mutableListOf<String>()
        val substringMatches = mutableListOf<String>()

        for (entry in emojiKeywordDatabase) {
            var matched = false
            for (kw in entry.keywords) {
                if (kw == trimmed) {
                    exactMatches.add(entry.emoji)
                    matched = true
                    break
                } else if (kw.startsWith(trimmed)) {
                    prefixMatches.add(entry.emoji)
                    matched = true
                    break
                } else if (kw.contains(trimmed)) {
                    substringMatches.add(entry.emoji)
                    matched = true
                    break
                }
            }
        }

        results.addAll(exactMatches.distinct())
        results.addAll(prefixMatches.distinct().filter { !results.contains(it) })
        results.addAll(substringMatches.distinct().filter { !results.contains(it) })

        return results
    }

}
