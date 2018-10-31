
import io.ktor.application.*
import io.ktor.content.PartData
import io.ktor.content.readAllParts
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Date
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.awt.SystemColor.text
import java.text.DateFormat


object Users: IntIdTable() {
    val login = varchar("login", 20)
    val password = varchar("password", 20)
    val firstName = varchar("firstName", 20)
    val lastName = varchar("lastName", 20)
    val birthday = date("birthday")
    val sex = bool("sex")
    val employment = varchar("employment", 30)
}


class UserEntry(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserEntry>(Users)
    var login by Users.login
    var password by Users.password
    var firstName by Users.firstName
    var lastName by Users.lastName
    var birthday by Users.birthday
    var sex by Users.sex
    var employment by Users.employment
}

fun Application.main() {
    Database.connect(System.getenv("JDBC_DATABASE_URL"), driver = "org.postgresql.Driver")
    transaction {
        create(Users)
//        if (UserEntry.count() == 0) {
//            UserEntry.new {
//                text = "Thank you for stopping by!"
//                creation = DateTime.now()
//            }
//        }
    }
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(CallLogging)
    install(Routing) {
        get("/") {
            val entries = transaction { UserEntry.all().toList() }
            call.respondText {
                var s = ""
                for (i in entries) {
                    s += " " + i.firstName
                }
                return@respondText s
            }

        }
        post("/register") {
            val user = call.receive<UserEntry>()
            transaction {
                Users.insert {
                    it[login] = user.login
                    it[password] = user.password
                    it[firstName] = user.firstName
                    it[lastName] = user.lastName
                    it[birthday] = user.birthday
                    it[sex] = user.sex
                    it[employment] = user.employment

                }
        }
    }}
}
