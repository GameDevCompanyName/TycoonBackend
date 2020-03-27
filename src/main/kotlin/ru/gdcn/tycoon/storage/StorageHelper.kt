package ru.gdcn.tycoon.storage

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

import ru.gdcn.tycoon.JSONHelper
import ru.gdcn.tycoon.storage.entity.User
import ru.gdcn.tycoon.storage.repository.UserRepositoryImpl


object StorageHelper {
    val sessionFactory: SessionFactory by lazy {
        val configuration = Configuration()
        configuration.addAnnotatedClass(User::class.java)
        configuration.setProperty("hibernate.connection.username", JSONHelper.env[JSONHelper.KEY_DB_USERNAME])
        configuration.setProperty("hibernate.connection.password", JSONHelper.env[JSONHelper.KEY_DB_PASSWORD]);
        configuration.configure()
        val builder = StandardServiceRegistryBuilder().applySettings(configuration.getProperties())
        configuration.buildSessionFactory(builder.build())
    }

    val userRepository: UserRepositoryImpl by lazy {
        UserRepositoryImpl()
    }
}
