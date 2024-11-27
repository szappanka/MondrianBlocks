package bme.aut.panka.mondrianblocks.data.user

import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun insert(user: User): Long {
        return userDao.addUser(user)
    }

    fun getAllUsers() = userDao.getAllUsers()

    suspend fun getUserById(id: String) = userDao.getUserById(id)

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
}