#!/usr/bin/env ruby

class ProcPing

	def initialize(name, data, qtdMsg)
		@name = name
		@data = data
		@qtdMsg = qtdMsg
		@mailBox = Array.new
		@i = -1
	end

	def setPeer(peer)
		@peer = peer
	end
	
	def setMailBox(data)
		@mailBox = data.clone
	end
			
	def send()
		@peer.setMailBox(@data)
	end

	def recv()
		while (true)
			if (@mailBox != nil) and (@mailBox.length == @data.length)
				@mailBox= Array.new
				break
			end 
		end
	end

	def start
		while (true)
			send()
			if(@i < @qtdMsg - 1)
				recv()
			else
				break
			end
			@i = @i + 1
		end
	end
end

class PingPing
	def initialize(tamMsg, qtdMsg, pairsN)
		@tamMsg = tamMsg
		@qtdMsg = qtdMsg
		@pairsN = pairsN
	end

	def start
			t = Thread.new do
			array = Array.new(@tamMsg, 1)
			pairs = Array.new()
			count = 0

			time1 = Time.now
			while count < @pairsN
				p1 = ProcPing.new("1", array, @qtdMsg)
				p2 = ProcPing.new("2", array, @qtdMsg)
				pairs << [ p1, p2 ]
				count +=1
			end
			time2 = Time.now
			timeSpawn = time2 - time1

			count = 0
			time1 = Time.now
			while count < @pairsN
				(p1, p2) = pairs[count]

				p2.setPeer(p1)
				p1.setPeer(p2)

				t1 = Thread.new { p1.start }
				t2 = Thread.new { p2.start }

				t2.join
				t1.join

				count +=1
			end
			time2 = Time.now
			delta = time2 - time1

			line = "#{@tamMsg}\t#{@qtdMsg}\t#{@pairsN}\t#{"%.6f" % delta.to_f}"

			if File.exists?("temp.txt")
				file = File.open("temp.txt", "a")
				file.puts(line)
				file.close
			else
				file = File.open("temp.txt", "w")
				file.puts(line)
				file.close
			end
		end
		t.join
	end
end

if __FILE__ == $0
	
	tamMsg = ARGV[0].to_i
	qtdMsg = ARGV[1].to_i
	pairsN = ARGV[2].to_i

	pingPing = PingPing.new(tamMsg, qtdMsg, pairsN)
	pingPing.start
end
