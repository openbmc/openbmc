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
#include <sys/socket.h>
#include <sys/xattr.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char* argv[])
{
	int sock,ret;
	struct sockaddr_in server_addr, client_addr;
	socklen_t len;
	char message[5];
	char* label;
	char* attr = "security.SMACK64IPIN";
	int port;

	if(argc != 3)
	{
		perror("Server: Argument missing, please provide port and label for SMACK64IPIN");
		return 2;
	}
	
	port = atoi(argv[1]);
	label = argv[2];

	struct timeval timeout;
	timeout.tv_sec = 15;
	timeout.tv_usec = 0;

	sock = socket(AF_INET,SOCK_DGRAM,0);
	if(sock < 0)
	{
		perror("Server: Socket error");
		return 2;
	}
	

	if(fsetxattr(sock, attr, label, strlen(label), 0) < 0)
	{
		perror("Server: Unable to set attribute ");
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
		perror("Server: Bind failure");
		return 2;
	}

	len = sizeof(client_addr);
	ret = recvfrom(sock, message, sizeof(message), 0, (struct sockaddr*)&client_addr,
					&len);
	close(sock);
	if(ret < 0)
	{
		perror("Server: Error receiving");
		return 1;

	}
	return 0;
}

