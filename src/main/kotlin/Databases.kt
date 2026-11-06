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
    // Formato correcto para el driver JDBC de PostgreSQL
    val jdbcUrl = "jdbc:postgresql://shinkansen.proxy.rlwy.net:30166/railway?user=postgres&password=VGHatOaBfbmmcVzVcQXFVKgkOKDsbLvU"

    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver"
    )

    transaction {
        SchemaUtils.create(UsersTable)

        if (UsersTable.selectAll().count() == 0L) {
            UsersTable.insert {
                it[username] = "admin"
                it[password] = "1234"
                it[role] = "ADMIN"
            }
        }
    }
}