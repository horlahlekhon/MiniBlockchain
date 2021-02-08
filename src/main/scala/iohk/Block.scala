package iohk

import iohk.Base.{Nonce, Number}

import scala.util.control.NoStackTrace


case class ImproperlyMinedBlock(message: String) extends NoStackTrace {
  override def getMessage: String = message
}

case class Transaction(data: String) {
  def dataBytes: Array[Byte] = data.getBytes()
}


case class Block(index: Int, parentHash: Hash, transactions: Seq[Transaction], miningTargetNumber: Number, nonce: Nonce) {


  // The essence of PoW is that it is a problem whose solution is easy
  // (in computational resources) to verify but difficult to find.
  def verifyThisHasBeenMinedProperly: Boolean = cryptoHash.toNumber < miningTargetNumber

  // To get the crypto hash of the block, just feed all fields to SHA-256.
  def cryptoHash: Hash = {
    val txBytes: Array[Byte] = transactions.flatMap(e => e.dataBytes).toArray
    Sha256(Array(index.toByte), parentHash.toNumber.toByteArray, txBytes, Array(nonce.toByte))
  }

}

object Block {
  def apply(index: Int, parentHash: Hash, transactions: Seq[Transaction], miningTargetNumber: Number, nonce: Nonce): Block = {
    val blk = new Block(index, parentHash, transactions, miningTargetNumber, nonce)
    if (blk.verifyThisHasBeenMinedProperly) blk
    else throw ImproperlyMinedBlock("Block was not properly mined")
  }
}


