package bme.aut.panka.mondrianblocks.data.block

import javax.inject.Inject

class BlockRepository @Inject constructor(private val blockDao: BlockDao) {
    suspend fun addBlock(block: Block) = blockDao.addBlock(block)
    fun getAllBlocks() = blockDao.getAllBlocks()
}