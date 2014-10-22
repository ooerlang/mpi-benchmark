#!/usr/bin/env python
# -*- coding: latin-1 -*-

import sys
import datetime
from threading import Thread
from threading import BoundedSemaphore

semaphore = BoundedSemaphore()
global_count = 0

class ProcPing(Thread):
	def __init__(self, name, data, qtdMsg):
		Thread.__init__(self)
		self.name = name
		self.data = data
		self.qtdMsg = qtdMsg
		self.mailBox = []

	def setPeer(self, Peer):
		self.Peer = Peer

	def send(self, dado):
		self.Peer.mailBox = dado

	def recv(self):
		while True:
			if not len(self.mailBox) < len(self.data):
				self.mailBox = []
				break

	def run(self):
		global global_count
		for i in range (0, self.qtdMsg + 1):
			self.send(self.data)
			if i < self.qtdMsg:
				self.recv()
		semaphore.acquire()
		global_count -= 1
		semaphore.release()

class ProcPong(Thread):
	def __init__(self, name, data, qtdMsg):
		Thread.__init__(self)
		self.name = name
		self.data = data
		self.qtdMsg = qtdMsg
		self.mailBox = []

	def setPeer(self, Peer):
		self.Peer = Peer

	def send(self, dado):
		self.Peer.mailBox = dado

	def recv(self):
		while True:
			if not len(self.mailBox) < len(self.data):
				self.mailBox = []
				break

	def run(self):
		global global_count
		for i in range (0, self.qtdMsg + 1):
			self.recv()
			if i < self.qtdMsg:
				self.send(self.data)
		semaphore.acquire()
		global_count -= 1
		semaphore.release()

class PingPing(Thread):

	def __init__(self, tamMsg, qtdMsg, PairsN):
		Thread.__init__(self)
		self.tamMsg = tamMsg
		self.qtdMsg = qtdMsg
		self.PairsN = PairsN

	def run(self):
		global global_count
		index = 0
		array = [1]
		pairs = []
		while index < self.tamMsg -1:
			array.append(1)
			index = index + 1

		timeStart = datetime.datetime.now()
		for pair in range(self.PairsN):
			p1 = ProcPing("1", array, self.qtdMsg)
			p2 = ProcPong("2", array, self.qtdMsg)
			pairs += [ (p1, p2), ]

		timeEnd = datetime.datetime.now()
		timeSpawn = timeEnd - timeStart

		timeStart = datetime.datetime.now()
		for p1, p2 in pairs:
			p2.setPeer(p1)
			p1.setPeer(p2)

			p1.start()
			p2.start()

		while global_count != 0:
			print global_count
			pass
		timeEnd = datetime.datetime.now()
		timeExec = timeEnd - timeStart

		line = "%d\t%d\t%s\t%s\n" % (self.tamMsg, self.qtdMsg, timeExec, timeSpawn)

		try:
			arq = open('saida.txt', 'r')
			textoSaida = arq.read()
			arq.close()
		except:
			arq = open('saida.txt', 'w')
			textoSaida = ""
			arq.close()

		arq = open('saida.txt', 'w')
		textoSaida = textoSaida + line
		arq.write(textoSaida)
		arq.close()

def main():
	param = sys.argv[1:]

	tamMsg = int(param[0])
	qtdMsg = int(param[1])
	PairsN = int(param[2])

	pingPing = PingPing(tamMsg, qtdMsg, PairsN)
	global global_count
	global_count = PairsN*2
	pingPing.start()

if __name__=="__main__":
	main()
