package iohk

import iohk.Base._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Random

object Mining {

  /* NOTE: A Hash is also a Number, we use the two interchangeably.

   Mining is about computing hashes until we get something that is less
   than a given target number.
   This target serves, in a way, as the maximum possible number that a
   proof of work computation should produce.*/
  final val StdMiningTargetNumber = targetByLeadingZeros(1)


  /* Whoa! We actually mine the Genesis block.
   Normally, this is done by the system during bootstrapping
   and every other block is mined by a miner.*/
  final val Genesis =
    Mining.mineNextBlock(
      index = 0, // The very first block
      parentHash = Sha256.Zero_Hash, // Let's assume this is by definition for the Genesis block.
      transactions = Seq(Transaction("Hello Blockchain, this is Genesis :)")),
      StdMiningTargetNumber,
    )


  /* We basically create a target number with the requirement of having
   some leading zeros. More leading zeros means smaller number target number.

   NOTE: To actually solve the current coding challenge, would you choose a
   small or a big number of leading zeros?*/
  def targetByLeadingZeros(zeros: Int): BigInt = {
    require(zeros < Sha256.NumberOfBytes)

    val bytes: Bytes =
      Array.tabulate[Byte](32) { n =>
        if (n < zeros) 0
        else 0xff.toByte
      }
    BigInt(1, bytes)
  }


  /* And now let's implement the actual "proof-of-work"-style computation.
   Compare the parameters of this method with the fields of a Block and
   you'll see that the only thing missing here is the nonce. Here is why.

   Initially we have all the fixed elements a block:

    - index,
    - parentHash,
    - transactions,
    - miningTargetNumber

   and by varying the nonce we try to have a block hash that is below the
   given miningTargetNumber.

   NOTE Remember that the block hash can be transformed to an actual number,
        so we can talk about hash and number interchangeably.*/
  def mineNextBlock(index: Int, parentHash: Hash, transactions: Seq[Transaction], miningTargetNumber: BigInt): Future[Block] = {
    var nonceValue = Promise[Long]
    def mineBlock(): Unit = {
      var nonceGotten = false
      while (!nonceGotten) {
        val nonce = new Random().nextLong() + 1
        val blk = new Block(index, parentHash, transactions, miningTargetNumber, nonce)
        if (blk.cryptoHash.toNumber < miningTargetNumber) {
          nonceGotten = true
          nonceValue = Promise.successful(nonce)
        }
      }
    }

    Future.sequence(Seq.fill(2)(Future(mineBlock())))
      .flatMap { _ =>
        nonceValue.future.map { nnce =>
          Block(index, parentHash, transactions, miningTargetNumber, nnce)
        }
      }
  }

  def numberOfLeadingZeros(amount: Int, hash: Hash): Boolean = hash.bytes.count(_.toChar == '0') == amount


}


