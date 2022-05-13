#!/bin/sh

SIG_FILE="/tmp/bmc.sig"
IMAGE_FILE="/tmp/bios-image"
BURN_IMAGE="/tmp/image-bios"
publickey="/etc/activationdata/OpenBMC/publickey"
bioslog="/tmp/update-bios.log"

echo "Verify bios image..."

if [ -f $publickey ];then
	r=$(openssl dgst -verify $publickey -sha256 -signature $SIG_FILE $IMAGE_FILE)
	echo "$r" > $bioslog
	if [ "Verified OK" == "$r" ]; then
        echo "bios image verify ok."
        mv $IMAGE_FILE $BURN_IMAGE
        rm -f $SIG_FILE
        exit 0
	else
        echo "bios image verify fail."
        rm -f $IMAGE_FILE
        echo "Remove bios image"
        exit 1
	fi
else
	echo "No $publickey file" > $bioslog
	exit 1
fi
