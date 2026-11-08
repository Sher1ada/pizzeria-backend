package com

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// 1. Definición de la Tabla de Usuarios en PostgreSQL
object UsersTable : Table("users") {
    val username = varchar("username", 50)
    val password = varchar("password", 50)
    val role = varchar("role", 20)

    override val primaryKey = PrimaryKey(username)
}

// 2. Función para conectar a Railway e insertar datos iniciales
fun initDatabase() {
    // Lee la URL de Railway o usa la de respaldo
    val rawUrl = System.getenv("DATABASE_URL")
        ?: "postgresql://postgres:VGHatOaBfbmmcVzVcQXFVKgkOKDsbLvU@shinkansen.proxy.rlwy.net:30166/railway"

    // Formatear para el driver JDBC (debe iniciar con jdbc:postgresql://)
    val jdbcUrl = when {
        rawUrl.startsWith("jdbc:") -> rawUrl
        rawUrl.startsWith("postgres://") -> rawUrl.replace("postgres://", "jdbc:postgresql://")
        rawUrl.startsWith("postgresql://") -> "jdbc:$rawUrl"
        else -> "jdbc:postgresql://$rawUrl"
    }

    val dbUser = System.getenv("PGUSER") ?: "postgres"
    val dbPassword = System.getenv("PGPASSWORD") ?: "VGHatOaBfbmmcVzVcQXFVKgkOKDsbLvU"

    // Conectar pasando credenciales explícitas a Exposed
    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

    // Crear esquemas e insertar datos de prueba
    transaction {
        SchemaUtils.create(UsersTable)

        if (UsersTable.selectAll().empty()) {
            UsersTable.insert {
                it[username] = "admin"
                it[password] = "1234"
                it[role] = "ADMIN"
            }
        }
    }
}