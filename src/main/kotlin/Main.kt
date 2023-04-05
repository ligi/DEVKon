import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.ipfs.kotlin.defaults.LocalIPFS
import java.io.File

fun main() {

    File("../api/data/sessions/").listFiles()?.forEach { edition ->
        println("Entering $edition")
        edition.listFiles { _, fileName -> fileName.endsWith(".json") }?.forEach { session ->
            println("processing $session")
            val obj = Parser.default().parse(session.inputStream()) as JsonObject
            val mp4file = File(edition, session.nameWithoutExtension + ".mp4")
            if (mp4file.exists()) {
                val hash = LocalIPFS().add.file(mp4file).Hash
                val oldHash = obj["sources_ipfsHash"]
                if (hash != oldHash) {

                    if (oldHash == null) {
                        println("initial hash")
                        val newJson = session.readText().replace("\"sources_youtubeId\"","\"sources_ipfsHash\": \"$hash\",\n  \"sources_youtubeId\"")
                        session.writeText(newJson)
                    } else {
                        println("hash changed")
                        val newJson = session.readText().replace(oldHash as String, hash)
                        session.writeText(newJson)
                    }
                }
                execute(edition, "ipfs-cluster-ctl pin add --name ${edition.name}:${session.name} $hash")
            } else {
                println("Downloading $session")
                execute(edition, "yt-dlp -f mp4 -o ${session.nameWithoutExtension}.%(ext)s ${obj["sources_youtubeId"]}")
            }
        }
    }
}

private fun execute(directory: File?, cmd: String) {
    val process = ProcessBuilder(*(cmd.split("\\s".toRegex()).toTypedArray()))
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectInput(ProcessBuilder.Redirect.INHERIT)
        .directory(directory)
        .start()

    process.waitFor()
}