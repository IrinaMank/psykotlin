
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


object GuestbookEntries: IntIdTable() {
    val text = varchar("text", 255)
    val creation = date("creation")
}

class GuestbookEntry(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<GuestbookEntry>(GuestbookEntries)
    var text by GuestbookEntries.text
    var creation by GuestbookEntries.creation
}

fun Application.main() {
    Database.connect(System.getenv("DATABASE_URL"), driver = "org.postgresql.Driver")
    transaction {
        create(GuestbookEntries)
        if(GuestbookEntry.count() == 0) {
            GuestbookEntry.new {
                text = "Thank you for stopping by!"
                creation = DateTime.now()
            }
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            val text = "Howdy, Planet!"
            call.respondText(text)
        }
    }
}
