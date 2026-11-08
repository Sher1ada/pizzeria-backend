package com

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

// 1. Definición de la Tabla de Usuarios
object UsersTable : Table("users") {
    val username = varchar("username", 50)
    val password = varchar("password", 50)
    val role = varchar("role", 20)

    override val primaryKey = PrimaryKey(username)
}

// 2. Función para conectar a Railway e insertar datos iniciales
fun initDatabase() {
    val databaseUrl = System.getenv("DATABASE_URL")

    val (jdbcUrl, dbUser, dbPassword) = if (!databaseUrl.isNullOrEmpty()) {
        val cleanUrl = if (databaseUrl.startsWith("jdbc:")) databaseUrl.removePrefix("jdbc:") else databaseUrl
        val uri = URI(cleanUrl)
        val userInfo = uri.userInfo?.split(":")
        val user = userInfo?.getOrNull(0) ?: System.getenv("PGUSER") ?: "postgres"
        val password = userInfo?.getOrNull(1) ?: System.getenv("PGPASSWORD") ?: ""
        val host = uri.host
        val port = if (uri.port != -1) uri.port else 5432
        val path = uri.path

        Triple("jdbc:postgresql://$host:$port$path", user, password)
    } else {
        Triple(
            "jdbc:postgresql://shinkansen.proxy.rlwy.net:30166/railway",
            System.getenv("PGUSER") ?: "postgres",
            System.getenv("PGPASSWORD") ?: "VGHatOaBfbmmcVzVcQXFVKgkOKDsbLvU"
        )
    }

    println(">>> CONECTANDO A POSTGRESQL: $jdbcUrl")

    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

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

    println(">>> BASE DE DATOS INICIALIZADA CORRECTAMENTE <<<")
}