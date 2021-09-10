#!/bin/sh

# Copyright (c) 2019-present Lenovo
# Copyright (c) 2020 Quanta Computer Inc.
# Licensed under BSD-3, see COPYING.BSD file for details.

IMAGE_FILE="/tmp/bios-image"
SIG_FILE="/tmp/bmc.sig"
BURN_IMAGE="/tmp/image-bios"
sha256_image="FFFF"
sha256_file="EEEE"

echo "Verify bios image..."

if [ -e $IMAGE_FILE ] && [ -e $SIG_FILE ];
then
    sha256_image=`sha256sum "$IMAGE_FILE" | awk '{print $1}'`
    sha256_file=`awk '{print $1}' $SIG_FILE`
fi

if [[ $sha256_image != $sha256_file ]];
then
    echo "bios image verify fail."
    rm -f $IMAGE_FILE
    echo "Remove bios image"
    exit 1
else
    echo "bios image verify ok."
    mv $IMAGE_FILE $BURN_IMAGE
    rm -f $SIG_FILE
    exit 0
fi
