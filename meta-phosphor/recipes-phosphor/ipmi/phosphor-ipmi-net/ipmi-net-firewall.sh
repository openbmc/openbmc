#!/bin/sh

if [ -f $IPTABLESRULE ]; then
    iptables-restore < $IPTABLESRULE
fi
