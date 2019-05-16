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
#include <stdio.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

int main(int argc, char* argv[])
{
	char* message = "hello";
	int sock, ret;
	struct sockaddr_in server_addr;
	struct hostent*  host = gethostbyname("localhost");
	char* label;
	char* attr = "security.SMACK64IPOUT";
	int port;
	if (argc != 3)
	{
		perror("Client: Argument missing, please provide port and  label for SMACK64IPOUT");
		return 2;
	}

	port = atoi(argv[1]);
	label = argv[2];
	sock = socket(AF_INET, SOCK_DGRAM,0);
	if(sock < 0)
	{
		perror("Client: Socket failure");
		return 2;
	}
	

	if(fsetxattr(sock, attr, label, strlen(label),0) < 0)
	{
		perror("Client: Unable to set attribute ");
		return 2;
	}


	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(port);
	bcopy((char*) host->h_addr, (char*) &server_addr.sin_addr.s_addr,host->h_length);
	bzero(&(server_addr.sin_zero),8);
	
	ret = sendto(sock, message, strlen(message),0,(const struct sockaddr*)&server_addr,
			sizeof(struct sockaddr_in));

	close(sock);
	if(ret < 0)
	{
		perror("Client: Error sending message\n");
		return 1;
	}
	
	return 0;
}

