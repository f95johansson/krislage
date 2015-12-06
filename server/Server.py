#!/usr/bin/env python

'''
    Simple web server
'''

import socket
import thread
import threading
from ClientThread import ClientThread

host = ''
port = 1337
backlog = 5 
size = 1024 
clients = []

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)

print 'outrageous-kiwi webb server'
print 'version 0.5'
print 'powered by python'
print 'Original author: Linus Lagerhjelm'
print '\nawaiting connection...'

while True:
    socket, address = s.accept()
    c = ClientThread(socket, address)
    print address,' connected'
    clients.append(c)
    thread.start_new_thread(c.run, ())
