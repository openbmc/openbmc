#!/usr/bin/env sh

# Network namespace name
NETNS="defeip"

# Create namespace
echo "Creating network namespace: $NETNS"
ip netns add $NETNS

# Transfer ethdfl to namespace-defeip
ip link set ethdfl netns $NETNS
ip link set ethdtl netns $NETNS

# iptables rules
ip netns exec $NETNS iptables -t mangle -N DIVERT
ip netns exec $NETNS iptables -t mangle -A PREROUTING -i ethdfl -j DIVERT
ip netns exec $NETNS iptables -t mangle -A DIVERT -j ACCEPT
ip netns exec $NETNS iptables -t mangle -A POSTROUTING -j MARK --set-mark 0x06200001
ip netns exec $NETNS iptables -t mangle -A POSTROUTING -j ACCEPT

# ip6tables rules: make convention even-rule v6
ip netns exec $NETNS ip6tables -t mangle -N DIVERT
ip netns exec $NETNS ip6tables -t mangle -A PREROUTING -i ethdfl -j DIVERT
ip netns exec $NETNS ip6tables -t mangle -A DIVERT -j ACCEPT
ip netns exec $NETNS ip6tables -t mangle -A POSTROUTING -j MARK --set-mark 0x06200002
ip netns exec $NETNS ip6tables -t mangle -A POSTROUTING -j ACCEPT

# v6:
# disable dad on ethdfl/ethdtl, ensure addresses assigned become permanent and dont wait on dad completion
# as they will nvr directly go on wire
ip netns exec $NETNS sysctl -w net.ipv6.conf.ethdfl.accept_dad=0
ip netns exec $NETNS sysctl -w net.ipv6.conf.ethdtl.accept_dad=0

# Assign ip to interfaces ethdfl
ip netns exec $NETNS ip addr add 172.168.0.10/24 dev ethdfl
ip netns exec $NETNS ip addr add 182.168.0.10/24 dev ethdtl

# v6
ip netns exec $NETNS ip -6 addr add 2002:0db8:abcd:0002::1/64 dev ethdfl
ip netns exec $NETNS ip -6 addr add 2001:0db8:abcd:0001::10/64 dev ethdtl

# Configure ip rule and ip route
ip netns exec $NETNS sysctl -w net.ipv4.ip_forward=1
sysctl -w net.ipv4.ip_forward=1

# v6
ip netns exec $NETNS sysctl -w net.ipv6.conf.all.forwarding=1
sysctl -w net.ipv6.conf.all.forwarding=1

# Bring up interface 
ip netns exec $NETNS ip link set ethdfl up
ip netns exec $NETNS ip link set ethdtl up

# Set default route for marked pkt.
ip netns exec $NETNS ip route add default via 182.168.0.1 dev ethdtl

# set static ARP
ip netns exec $NETNS arp -s 182.168.0.1 aa:cd:ef:ab:cd:ef

# v6
ip netns exec $NETNS ip -6 route add default via 2001:0db8:abcd:0001::1 dev ethdtl
ip netns exec $NETNS ip -6 neigh add 2001:0db8:abcd:0001::1 lladdr aa:cd:ef:ab:cd:ef dev ethdtl

echo "Network namespace configuration complete"
