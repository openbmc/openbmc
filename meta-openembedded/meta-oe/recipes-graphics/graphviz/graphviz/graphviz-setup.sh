#!/bin/sh

echo "Setting up graphviz..."

# Create /usr/lib/graphviz/config6
$OECORE_NATIVE_SYSROOT/usr/bin/dot -c
