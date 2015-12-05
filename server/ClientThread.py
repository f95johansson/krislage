class ClientThread:

    def __init__(self, socket, address):
        self.socket = socket
        self.address = address
    def run(self):
        run = True
        while run:
            data = self.socket.recv(1024) #might have to be increased
            if data:
                with open("data", "a") as f:
                    print 'recieved data of size', len(data)
                    data = data + '\n'
                    f.write(data)
                    f.close()
            else:
                run = False

        self.socket.close()
    
