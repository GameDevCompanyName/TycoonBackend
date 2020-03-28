package ru.gdcn.tycoon.storage

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import ru.gdcn.tycoon.storage.entity.*

import ru.gdcn.tycoon.util.EnvReader
import ru.gdcn.tycoon.storage.repository.*
import ru.gdcn.tycoon.storage.repository.base.*


object StorageHelper {
    val sessionFactory: SessionFactory by lazy {
        val configuration = Configuration()
        configuration.addAnnotatedClass(User::class.java)
        configuration.addAnnotatedClass(Player::class.java)
        configuration.addAnnotatedClass(City::class.java)
        configuration.addAnnotatedClass(Resource::class.java)
        configuration.addAnnotatedClass(PlayerResource::class.java)
        configuration.addAnnotatedClass(CityResource::class.java)
        configuration.setProperty("hibernate.connection.username", EnvReader.env[EnvReader.KEY_DB_USERNAME])
        configuration.setProperty("hibernate.connection.password", EnvReader.env[EnvReader.KEY_DB_PASSWORD]);
        configuration.configure()
        val builder = StandardServiceRegistryBuilder().applySettings(configuration.properties)
        configuration.buildSessionFactory(builder.build())
    }

    val userRepository: IUserRepository by lazy {
        UserRepositoryImpl()
    }

    val playerRepository: IPlayerRepository by lazy {
        PlayerRepositoryImpl()
    }

    val cityRepository: ICityRepository by lazy {
        CityRepositoryImpl()
    }

    val resourceRepository: IResourceRepository by lazy {
        ResourceRepositoryImpl()
    }

    val cityResourceRepository: ICityResourceRepository by lazy {
        CityResourceRepositoryImpl()
    }

    val playerResourceRepository: IPlayerResourceRepository by lazy {
        PlayerResourceRepositoryImpl()
    }
}
