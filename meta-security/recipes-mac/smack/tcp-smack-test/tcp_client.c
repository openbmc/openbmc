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
#include <sys/socket.h>
#include <sys/types.h>
#include <errno.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netdb.h>
#include <string.h>
#include <sys/xattr.h>

int main(int argc, char* argv[])
{

	int sock;
	char message[255] = "hello";
	struct sockaddr_in server_addr;
	char* label_in;
	char* label_out;
	char* attr_out = "security.SMACK64IPOUT";
	char* attr_in = "security.SMACK64IPIN";
	char out[256];
	int port;

	struct timeval timeout;
	timeout.tv_sec = 15;
	timeout.tv_usec = 0;

	struct hostent*  host = gethostbyname("localhost");

	if (argc != 4)
	{
		perror("Client: Arguments missing, please provide socket labels");
		return 2;
	}

	port = atoi(argv[1]);
	label_in = argv[2];
	label_out = argv[3];

	if((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
		perror("Client: Socket failure");
		return 2;
	}


	if(fsetxattr(sock, attr_out, label_out, strlen(label_out), 0) < 0)
	{
		perror("Client: Unable to set attribute SMACK64IPOUT");
		return 2;
	}

	if(fsetxattr(sock, attr_in, label_in, strlen(label_in), 0) < 0)
	{
		perror("Client: Unable to set attribute SMACK64IPIN");
		return 2;
	}

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(port);
	bcopy((char*) host->h_addr, (char*) &server_addr.sin_addr.s_addr,host->h_length);
	bzero(&(server_addr.sin_zero),8);
	
	if(setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) < 0)
	{
		perror("Client: Set timeout failed\n");
		return 2;
	}
	
	if (connect(sock, (struct sockaddr *)&server_addr,sizeof(struct sockaddr)) == -1)
	{
    		perror("Client: Connection failure");
			close(sock);
        	return 1;
	}


	if(write(sock, message, strlen(message)) < 0)
	{
		perror("Client: Error sending data\n");
		close(sock);
		return 1;
	}
	close(sock);
	return 0;
}






