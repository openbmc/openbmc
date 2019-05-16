#!/bin/sh
RC=0
test_file=/tmp/smack_socket_tcp
SMACK_PATH=`grep smack /proc/mounts | awk '{print $2}' `
# make sure no access is granted
#        12345678901234567890123456789012345678901234567890123456
echo -n "label1                  label2                  -----" > $SMACK_PATH/load

tcp_server=`which tcp_server`
if [ -z $tcp_server ]; then
	if [ -f "/tmp/tcp_server" ]; then
		tcp_server="/tmp/tcp_server"
	else
		echo "tcp_server binary not found"
		exit 1
	fi
fi
tcp_client=`which tcp_client`
if [ -z $tcp_client ]; then
	if [ -f "/tmp/tcp_client" ]; then
		tcp_client="/tmp/tcp_client"
	else
		echo "tcp_client binary not found"
		exit 1
	fi
fi

# checking access for sockets with different labels
$tcp_server 50016 label1 &>/dev/null &
server_pid=$!
sleep 2
$tcp_client 50016 label2 label1 &>/dev/null &
client_pid=$!

wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?

if [ $server_rv -eq 0 -o $client_rv -eq 0 ]; then
	echo "Sockets with different labels should not communicate on tcp"
	exit 1
fi

# granting access between different labels
#        12345678901234567890123456789012345678901234567890123456
echo -n "label1                  label2                  rw---" > $SMACK_PATH/load
# checking access for sockets with different labels, but having a rule granting rw
$tcp_server 50017 label1 2>$test_file &
server_pid=$!
sleep 1
$tcp_client 50017 label2 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Sockets with different labels, but having rw access, should communicate on tcp"
	exit 1
fi

# checking access for sockets with the same label
$tcp_server 50018 label1 2>$test_file &
server_pid=$!
sleep 1
$tcp_client 50018 label1 label1  2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Sockets with same labels should communicate on tcp"
	exit 1
fi

# checking access on socket labeled star (*)
# should always be permitted
$tcp_server 50019 \* 2>$test_file &
server_pid=$!
sleep 1
$tcp_client 50019 label1 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Should have access on tcp socket labeled star (*)"
	exit 1
fi

# checking access from socket labeled star (*)
# all access from subject star should be denied
$tcp_server 50020 label1 2>$test_file &
server_pid=$!
sleep 1
$tcp_client 50020 label1 \* 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -eq 0 -o  $client_rv -eq 0 ]; then
	echo "Socket labeled star should not have access to any tcp socket"
	exit 1
fi
