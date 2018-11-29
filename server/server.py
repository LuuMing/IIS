#!/usr/bin/env python
# -*- coding:utf-8 -*-
import socketserver
import json
import os
import sys
from libs.helper import *

config = None

class MyServer(socketserver.BaseRequestHandler):
        
	def handle(self):
		print(self.request,self.client_address,self.server)
		conn = self.request
		with open('config.ini', 'w') as configfile:
        		config.write(configfile)
		if sys.platform == 'win32':
			os.system('tools\ini2json.exe config.ini')
		else:
			os.system('./tools/ini2json config.ini')
		with open('config.json','r') as f:
			respons = json.loads(f.read())
		respons = json.dumps(respons)
		conn.sendall(respons.encode())
		print(respons,'sended')
		flag = True
		while flag:
			raw_data = conn.recv(1024).decode()
			data = json.loads(raw_data)
			print(data,'receive')
			if data['name'] == 'exit':
				flag = False
			else:
				component = config[data['name']]
				if component['type'] == 'action':
					if component['source']	== 'os':
						os.system(component['cmd'])
						respons = {'content':'success'}

				elif component['type'] == 'status':
					if component['source'] == 'os':
						respons = {'content':os.popen(component["cmd"]).read()}
			respons = json.dumps(respons)
			conn.sendall(respons.encode())
			print(respons,'sended')

if __name__ == '__main__':
	config = get_config()
	server = socketserver.ThreadingTCPServer(('',6666),MyServer)
	server.serve_forever()
