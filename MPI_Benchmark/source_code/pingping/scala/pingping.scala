import actors.Actor
import actors.Actor._
import java.io._
import scala.io.Source

object pingping{

	def main(args: Array[String]){
	
		var DataSize : Int = Integer.parseInt(args(0))
		var ReptNum  : Int = Integer.parseInt(args(1))	
		var PairNum  : Int = Integer.parseInt(args(2))
		var FileName       = args(3)
		var Data           = new Array[Byte](DataSize)

		var i = 0;
		while(i < DataSize)
		{
			Data(i) = 1;
			i = i +1;
		}

		var finalize = new Finalize(DataSize, ReptNum, PairNum, FileName)

    		var ping:Array[Ping] = new Array[Ping](PairNum * 2)

		for (i <- 0 until PairNum) {
			ping(2 * i) = new Ping(Data, ReptNum, finalize)
			ping(2 * i + 1) = new Ping(Data, ReptNum, finalize)
		}

		finalize.start
		for (i <- 0 until PairNum) {
			ping(2 * i).start
			ping(2 * i + 1).start
		}

		for (i <- 0 until PairNum) {
			ping(2 * i) ! ping(2 * i + 1)
			ping(2 * i + 1) ! ping(2 * i)
		}
	}
}

class Finalize(DataSize: Int, ReptNum: Int, PairNum : Int, FileName: String)
	extends Actor {
	def act(){
		var total = 0
		var start = System.nanoTime()
		loop{
			react{
				case 1 => {
					total = total + 1;
					if(total == 2 * PairNum) {
						var finish = System.nanoTime()
						var sub = finish - start
						try {
					 		var source = scala.io.Source.fromFile("../../docs/scala/" + FileName)
							var lines = source .mkString
							lines = lines + DataSize + "\t\t" + ReptNum + "\t\t" + PairNum + "\t\t" + sub/1000 + "\n"

							var writer = new FileWriter(new File("../../docs/scala/" + FileName))
							writer.write(lines);
							writer.close();
						}
						catch {
							case _ => {
								var writer = new FileWriter(new File("../../docs/scala/" + FileName))
						 		var lines = "DataSize\tRepetition\tPair\tTime[us]\n"	
						 		lines = lines + DataSize + "\t\t" + ReptNum + "\t\t" + PairNum + "\t\t" + sub/1000 + "\n";
						 		writer.write(lines);
						 		writer.close();
					 		}
						}
						exit('stop)
					}
				}
			}
		}
	}
}


class Ping(Data: Array[Byte], var Rep: Int, Main: Actor) extends Actor{	
	def act(){
		var MailBox : Array[Byte] = null
		loop{
			react{
				case value: Ping => {
					if(Rep > 0) {
						MailBox = Data.clone()
						value ! MailBox
						Rep = Rep -1
					} else{
						Main ! 1
						exit('stop)
					}
				}
				case value: Array[Byte] => {
					MailBox = Data.clone()
					if(Rep > 0) {
						sender ! MailBox
						Rep = Rep -1
					}
					else {
						Main ! 1
						exit('stop)
					}
				}
			}
		}
	}
}
