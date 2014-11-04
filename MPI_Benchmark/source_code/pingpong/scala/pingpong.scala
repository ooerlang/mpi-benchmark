import actors.Actor
import actors.Actor._
import java.io._
import scala.io.Source

object pingpong{

	def main(args: Array[String]) {
	
		var DataSize : Int = Integer.parseInt(args(0))
		var ReptNum  : Int = Integer.parseInt(args(1))
		var PairNum  : Int = Integer.parseInt(args(2))	
		var FileName = args(3)
		var Data = new Array[Byte](DataSize)
	
		var i = 0;
		while(i < DataSize) {
			Data(i) = 1;
			i = i +1;
		}

		var finalize = new Finalize(DataSize, ReptNum, PairNum, FileName)

    		var ping:Array[Ping] = new Array[Ping](PairNum)
		var pong:Array[Pong] = new Array[Pong](PairNum)

		for (i <- 0 until PairNum) {
			pong(i) = new Pong(Data)
			ping(i) = new Ping(Data, ReptNum, pong(i), finalize)
		}

		finalize.start
		for (i <- 0 until PairNum) {
			ping(i).start
			pong(i).start
		}
	}
}

class Finalize(DataSize: Int, ReptNum: Int, PairNum : Int, FileName: String) extends Actor {
	def act() {		
		var start = System.nanoTime()
		loop {
			react {
				case 1 => {
					var finish = System.nanoTime
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

class Ping(Data: Array[Byte], var Rep: Int, Pong: Actor, Main: Actor) extends Actor {	
	def act(){
		var MailBox : Array[Byte] = null
		Pong ! Data
		loop{
			react{
				case value: Array[Byte] => {
					if(Rep > 0) {
						MailBox = Data.clone();
						Pong ! MailBox
						Rep = Rep -1;
					} else {
						Pong ! 1
						Main ! 1
						exit('stop)
					}
				}
			}
		}
	}
}

class Pong(Data: Array[Byte]) extends Actor {	
	def act(){
		var MailBox : Array[Byte] = null
		loop{
			react{
				case value: Array[Byte] => {
					MailBox = Data.clone();
					sender ! MailBox
				}
				case 1 => {
					exit('stop)
				}
			}
		}
	}
}
