#!/bin/sh
gdb -q $1  <<'EOF'
b main
r
c
q
EOF
echo ""
