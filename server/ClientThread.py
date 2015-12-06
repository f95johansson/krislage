class ClientThread:

    def __init__(self, socket, address):
        self.socket = socket
        self.address = address
    def run(self):
        run = True
        while run:
            data = self.socket.recv(65535)
            if data:
                with open("data", "a") as f:
                    print 'recieved data of size', len(data)
                    if data[len(data)-1] == '}':
                        data = data + '\n'
                        f.write(data)
                        f.close()
                    else:
                        f.write(data)
                        f.close() 
            else:
                run = False

        self.socket.close()
    
