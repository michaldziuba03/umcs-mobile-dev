package com.example.zad6

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

class BookService : Service() {

    private val handler = Handler()
    private val books = listOf(
        mapOf("title" to "Frankenstein; Or, The Modern Prometheus", "content" to "\"Frankenstein; Or, The Modern Prometheus\" by Mary Wollstonecraft Shelley is a novel written in the early 19th century. The story explores themes of ambition, the quest for knowledge, and the consequences of man's hubris through the experiences of Victor Frankenstein and the monstrous creation of his own making. The opening of the book introduces Robert Walton, an ambitious explorer on a quest to discover new lands and knowledge in the icy regions of the Arctic. In his letters to his sister Margaret, he expresses both enthusiasm and the fear of isolation in his grand venture. As Walton's expedition progresses, he encounters a mysterious, emaciated stranger who has faced great suffering—furthering the intrigue of his narrative. This stranger ultimately reveals his tale of creation, loss, and the profound consequences of seeking knowledge that lies beyond human bounds. The narrative is set up in a manner that suggests a deep examination of the emotions and ethical dilemmas faced by those who dare to defy the natural order."),
        mapOf("title" to "Alice's Adventures in Wonderland", "content" to "\"Alice's Adventures in Wonderland\" by Lewis Carroll is a classic children's novel written in the mid-19th century. The story follows a young girl named Alice who, feeling bored and sleepy while sitting by a riverbank, encounters a White Rabbit and follows it down a rabbit hole, plunging into a fantastical world filled with curious creatures and whimsical adventures. The opening of the book introduces Alice as she daydreams about her surroundings before spotting the White Rabbit, who is both flustered and animated. Curious, Alice pursues the Rabbit and finds herself tumbling down a deep rabbit hole, leading to a curious hall filled with doors, all locked. After experiencing a series of bizarre changes in size from eating and drinking mysterious substances, she begins exploring this new world, initially frustrated by her newfound challenges as she navigates her size and the peculiar inhabitants she meets. The narrative sets the tone for Alice's whimsical and often nonsensical adventures that characterize the entire tale."),
        mapOf("title" to "Dracula", "content" to "Gothic horror novel written in the late 19th century. The story unfolds through a series of letters, journal entries, and newspaper clippings, primarily following the experiences of Jonathan Harker, a young English solicitor. Harker’s journey takes him to Transylvania, where he encounters the enigmatic Count Dracula, setting a thrilling and mysterious tone that delves into themes of fear, seduction, and the supernatural. The opening of the novel presents Jonathan Harker’s journal entries, marking the beginning of his travels to meet Count Dracula regarding a real estate transaction. Harker describes his train journey through the picturesque landscapes of eastern Europe, highlighting the eerie atmosphere and local superstitions that hint at the challenges he will face. Upon arriving at the Count's castle, Harker senses unease, especially when local villagers express concern and give him protective charms against evil spirits. The tension escalates as Harker meets Dracula, who, while courteous, exhibits strange and unsettling behavior. Kafkaesque and claustrophobic, the initial chapters effectively set the stage for Harker’s realization that he is trapped in Dracula’s world, creating an eerie, suspenseful foundation for the unfolding narrative."),
        mapOf("title" to "The Iliad", "content" to "epic poem traditionally attributed to the 8th century BC. The narrative centers around the breathtaking events of the Trojan War, focusing particularly on the wrath of Achilles, a Greek hero, as well as themes of honor, glory, and the interplay between mortals and deities. The work is often lauded as one of the cornerstones of Western literature, laying the foundation for numerous literary traditions and influences. The opening of \"The Iliad\" introduces readers to a world steeped in myth and legendary struggle, beginning with an invocation to the Muse, typical of epic poetry. It sets the stage for the conflict between the Greek and Trojan forces, highlighting Achilles' fierce pride and anguish over personal slights that lead to dire consequences on the battlefield. The text establishes a rich tapestry of characters, divine interventions, and emotional turmoil, beckoning readers to explore the complex relationships and moral dilemmas faced by the warriors of both sides. This immersion into the epic's grand themes and character-driven drama makes the opening a captivating precursor to the intense narrative that unfolds throughout the poem."),
        mapOf("title" to "Metamorphosis", "content" to "novella written during the late 19th century. The book explores themes of alienation and identity through the strange and tragic transformation of its main character, Gregor Samsa, who wakes up one morning to find himself turned into a grotesque insect. The story grapples with Gregor's struggle to adapt to his new physical form and its implications for his family, shedding light on societal expectations and familial responsibilities. The opening of \"Metamorphosis\" presents an unsettling scene as Gregor Samsa discovers his shocking metamorphosis. Initially confused and in discomfort, he reflects on his life as a travelling salesman and the burdens of his job, all while grappling with the absurdity of his situation. As he struggles to get out of bed, the concern of his family begins to stir. His mother knocks on his door, anxious about his tardiness for work, and this prompts a cascade of worry for Gregor about how his family will react to his transformation. Despite his efforts to communicate and reassure them, the fear and misunderstanding stemming from his condition quickly become apparent. This launch into the bizarre and tragic sets the tone for the exploration of alienation and familial dynamics that unfolds throughout the novella.")
    )
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BookService", "Service started")
        if (!isRunning) {
            isRunning = true
            simulateDownload()
        }
        return START_STICKY
    }

    private fun simulateDownload() {
        Log.d("BookService", "Simulate download")
        val randomBook = books.random()
        val title = randomBook["title"] ?: "Unknown"
        val content = randomBook["content"] ?: ""

        val wordCount = content.split(" ").size
        val charCount = content.length
        val mostCommonWord =
            content.split(" ").groupingBy { it }.eachCount().maxByOrNull { it.value }?.key

        handler.postDelayed({
            val broadcastIntent = Intent("com.example.BOOK_DOWNLOADED")
            broadcastIntent
                .putExtra("title", title)
                .putExtra("wordCount", wordCount)
                .putExtra("charCount", charCount)
                .putExtra("mostCommonWord", mostCommonWord)
            sendBroadcast(broadcastIntent)
            stopSelf()
        }, 2000)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
