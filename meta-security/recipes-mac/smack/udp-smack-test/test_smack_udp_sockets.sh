#!/bin/sh
RC=0
test_file="/tmp/smack_socket_udp"
SMACK_PATH=`grep smack /proc/mounts | awk '{print $2}' `

udp_server=`which udp_server`
if [ -z $udp_server ]; then
	if [ -f "/tmp/udp_server" ]; then
		udp_server="/tmp/udp_server"
	else
		echo "udp_server binary not found"
		exit 1
	fi
fi
udp_client=`which udp_client`
if [ -z $udp_client ]; then
	if [ -f "/tmp/udp_client" ]; then
		udp_client="/tmp/udp_client"
	else
		echo "udp_client binary not found"
		exit 1
	fi
fi

# make sure no access is granted
#        12345678901234567890123456789012345678901234567890123456
echo -n "label1                  label2                  -----" > $SMACK_PATH/load

# checking access for sockets with different labels
$udp_server 50021 label2 2>$test_file &
server_pid=$!
sleep 1
$udp_client 50021 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -eq 0 ]; then
	echo "Sockets with different labels should not communicate on udp"
	exit 1
fi

# granting access between different labels
#        12345678901234567890123456789012345678901234567890123456
echo -n "label1                  label2                  rw---" > $SMACK_PATH/load
# checking access for sockets with different labels, but having a rule granting rw
$udp_server 50022 label2 2>$test_file &
server_pid=$!
sleep 1
$udp_client 50022 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Sockets with different labels, but having rw access, should communicate on udp"
	exit 1
fi

# checking access for sockets with the same label
$udp_server 50023 label1 &
server_pid=$!
sleep 1
$udp_client 50023 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Sockets with same labels should communicate on udp"
	exit 1
fi

# checking access on socket labeled star (*)
# should always be permitted
$udp_server 50024 \* 2>$test_file &
server_pid=$!
sleep 1
$udp_client 50024 label1 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -ne 0 -o $client_rv -ne 0 ]; then
	echo "Should have access on udp socket labeled star (*)"
	exit 1
fi

# checking access from socket labeled star (*)
# all access from subject star should be denied
$udp_server 50025 label1 2>$test_file &
server_pid=$!
sleep 1
$udp_client 50025 \* 2>$test_file &
client_pid=$!
wait $server_pid
server_rv=$?
wait $client_pid
client_rv=$?
if [ $server_rv -eq 0 ]; then
	echo "Socket labeled star should not have access to any udp socket"
	exit 1
fi
