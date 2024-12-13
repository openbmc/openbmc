// (C) Copyright 2015 Intel Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/xattr.h>
#include <errno.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char* argv[])
{

	int sock;
	int clientsock;
	char message[255];
	socklen_t client_length;
	struct sockaddr_in server_addr, client_addr;
	char* label_in;
	char* attr_in = "security.SMACK64IPIN";
	int port;

	struct timeval timeout;
	timeout.tv_sec = 15;
	timeout.tv_usec = 0;

	if (argc != 3)
	{
		perror("Server: Argument missing please provide port and label for SMACK64IPIN");
		return 2;
	}
	
	port = atoi(argv[1]);
	label_in = argv[2];
	bzero(message,255);

	
	if((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
		perror("Server: Socket failure");
		return 2;
	}
	
	
	if(fsetxattr(sock, attr_in, label_in, strlen(label_in),0) < 0)
	{
		perror("Server: Unable to set attribute ipin 2");
		return 2;
	}

	server_addr.sin_family = AF_INET;         
	server_addr.sin_port = htons(port);     
	server_addr.sin_addr.s_addr = INADDR_ANY; 
 	bzero(&(server_addr.sin_zero),8); 

	if(setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) < 0)
	{
		perror("Server: Set timeout failed\n");
		return 2;
	}

	if(bind(sock, (struct sockaddr*) &server_addr, sizeof(server_addr)) < 0)
	{
		perror("Server: Bind failure ");
		return 2;
	}

	listen(sock, 1);
	client_length = sizeof(client_addr);

	clientsock = accept(sock,(struct sockaddr*) &client_addr, &client_length);

	if (clientsock < 0)
	{
		perror("Server: Connection failed");
		close(sock);
		return 1;
	}
	

	if(fsetxattr(clientsock, "security.SMACK64IPIN", label_in, strlen(label_in),0) < 0)
	{
		perror(" Server: Unable to set attribute ipin 2");
		close(sock);
		return 2;
	}

	if(read(clientsock, message, 254) < 0)
	{
		perror("Server: Error when reading from socket");
		close(clientsock);
		close(sock);
		return 1;
	}


	close(clientsock);
	close(sock);

	return 0;
}
