#!/bin/sh
#
# load keymap, if existing

KERNEL_MAJMIN=`uname -r | cut -d '.' -f 1,2`
if [ -e /etc/keymap-$KERNEL_MAJMIN.map ]; then
	loadkeys /etc/keymap-$KERNEL_MAJMIN.map
fi

if ( ls "/etc" | grep -q "keymap-extension-${KERNEL_MAJMIN}" )
then
	for extension in `ls -1 /etc/keymap-extension-$KERNEL_MAJMIN*` 
	do	
		loadkeys "$extension"
	done
fi
