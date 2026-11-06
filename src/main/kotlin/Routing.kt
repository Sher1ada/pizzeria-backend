package com

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// 1. Modelos de datos serializables (DTOs)
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val nombre: String? = null
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
// 2. Configuración general de rutas
fun Application.configureRouting() {
    routing {
        // Estado del servidor
        get("/") {
            call.respondText("Servidor Ktor de Pizzería activo y conectado a la base de datos.")
        }

        // Endpoint de Autenticación mediante PostgreSQL (Railway)
        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = transaction {
                UsersTable.selectAll().where {
                    (UsersTable.username eq request.username) and (UsersTable.password eq request.password)
                }.singleOrNull()
            }

            if (user != null) {
                call.respond(
                    HttpStatusCode.OK,
                    LoginResponse(
                        success = true,
                        message = "Inicio de sesión exitoso",
                        role = user[UsersTable.role],
                        nombre = user[UsersTable.username]
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    LoginResponse(
                        success = false,
                        message = "Credenciales incorrectas",
                        role = null,
                        nombre = null
                    )
                )
            }
        }

        // Endpoint del Catálogo de Productos (Sin imágenes)
        get("/products") {
            val sampleProducts = listOf(
                Product(
                    id = "1",
                    name = "Pizza Pepperoni",
                    description = "Masa tradicional, salsa de tomate, mozzarella y abundante pepperoni",
                    price = 14.99,
                    category = "Pizzas"
                ),
                Product(
                    id = "2",
                    name = "Pizza Hawaiana",
                    description = "Queso mozzarella, jamón ahumado y piña dulce fresca",
                    price = 13.99,
                    category = "Pizzas"
                ),
                Product(
                    id = "3",
                    name = "Refresco 600ml",
                    description = "Bebida embotellada fría a elegir",
                    price = 2.50,
                    category = "Bebidas"
                )
            )
            call.respond(HttpStatusCode.OK, sampleProducts)
        }
    }
}