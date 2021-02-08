package iohk

import iohk.Base.Unknown

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

trait Blockchain {
  // Add a block to the chain.
  // The return type is up to you, as explained in the definition of Unknown.
  def append(block: Block): Blockchain

  // Find a block by index.
  def findByIndex(index: Int): Option[Block]

  // Find a block by hash.
  def findByHash(hash: Hash): Option[Block]

  // Find a common ancestor between this blockchain and that blockchain.
  def common_ancestor(that: Blockchain): Boolean

  def lastBlock: Block

}

case class BadlyCreatedBlock(message: String) extends Exception(message) {

}

class FastBlockchain(val genesis: Block) extends Blockchain {

  private val chain: ListBuffer[Block] = ListBuffer(genesis)
  private val hashIndexMapping: mutable.Map[Number, Int] = mutable.Map[Number, Int]()

  override def append(block: Block): Blockchain = {
    if (block.index != chain.last.index + 1 || block.parentHash.toNumber != this.lastBlock.cryptoHash.toNumber) {
      throw BadlyCreatedBlock("Block's index or hash not aligned properly" +
        s"""
           |Last block in the chain hash: ${chain.last.cryptoHash.toNumber}
           |newBlock parent hash: ${block.parentHash.toNumber}
           |""".stripMargin
      )
    } else {
      chain.append(block)
      this.hashIndexMapping.put(block.cryptoHash.toNumber, block.index)
      this
    }
  }

  override def lastBlock: Block = chain.last

  override def findByIndex(index: Int): Option[Block] =
    if (contain(index)) Some(chain(index))
    else None

  def contain(i: Int): Boolean =
    chain.indices.contains(i)

  override def findByHash(hash: Hash): Option[Block] =
    hashIndexMapping.get(hash.toNumber) match {
      case Some(index) =>
        Some(chain(index))
      case None =>
        None
    }

  /*I dont really get the concept of common ancestors,
   so i assumed if the two chain's genesis block has the parentHash there might be a relation there somewhere
   */
  override def common_ancestor(that: Blockchain): Boolean = {
    that.findByIndex(0) match {
      case Some(value) =>
        value.parentHash == this.genesis.parentHash
      case None =>
        false
    }
  }

  override def toString: String = chain.mkString

  def length: Int = chain.length
}

object FastBlockchain {

  def apply()(implicit ec: ExecutionContext): Future[FastBlockchain] = {
    Mining.Genesis
      .map { gen =>
        val blk = new FastBlockchain(gen)
        blk
      }
  }
}

