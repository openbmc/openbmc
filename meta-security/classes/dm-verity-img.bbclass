# SPDX-License-Identifier: MIT
#
# Copyright (C) 2020 BayLibre SAS
# Author: Bartosz Golaszewski <bgolaszewski@baylibre.com>
#
# This bbclass allows creating of dm-verity protected partition images. It
# generates a device image file with dm-verity hash data appended at the end
# plus the corresponding .env file containing additional information needed
# to mount the image such as the root hash in the form of ell variables. To
# assure data integrity, the root hash must be stored in a trusted location
# or cryptographically signed and verified.
#
# Usage:
#     DM_VERITY_IMAGE = "core-image-full-cmdline" # or other image
#     DM_VERITY_IMAGE_TYPE = "ext4" # or ext2, ext3 & btrfs
#     IMAGE_CLASSES += "dm-verity-img"
#
# The resulting image can then be used to implement the device mapper block
# integrity checking on the target device.

# Process the output from veritysetup and generate the corresponding .env
# file. The output from veritysetup is not very machine-friendly so we need to
# convert it to some better format. Let's drop the first line (doesn't contain
# any useful info) and feed the rest to a script.
process_verity() {
    local ENV="$OUTPUT.env"

    # Each line contains a key and a value string delimited by ':'. Read the
    # two parts into separate variables and process them separately. For the
    # key part: convert the names to upper case and replace spaces with
    # underscores to create correct shell variable names. For the value part:
    # just trim all white-spaces.
    IFS=":"
    while read KEY VAL; do
        echo -ne "$KEY" | tr '[:lower:]' '[:upper:]' | sed 's/ /_/g' >> $ENV
        echo -ne "=" >> $ENV
        echo "$VAL" | tr -d " \t" >> $ENV
    done

    # Add partition size
    echo "DATA_SIZE=$SIZE" >> $ENV

    ln -sf $ENV ${IMAGE_BASENAME}-${MACHINE}.$TYPE.verity.env
}

verity_setup() {
    local TYPE=$1
    local INPUT=${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$TYPE
    local SIZE=$(stat --printf="%s" $INPUT)
    local OUTPUT=$INPUT.verity

    cp -a $INPUT $OUTPUT

    # Let's drop the first line of output (doesn't contain any useful info)
    # and feed the rest to another function.
    veritysetup --data-block-size=1024 --hash-offset=$SIZE format $OUTPUT $OUTPUT | tail -n +2 | process_verity
}

VERITY_TYPES = "ext2.verity ext3.verity ext4.verity btrfs.verity"
IMAGE_TYPES += "${VERITY_TYPES}"
CONVERSIONTYPES += "verity"
CONVERSION_CMD_verity = "verity_setup ${type}"
CONVERSION_DEPENDS_verity = "cryptsetup-native"

python __anonymous() {
    verity_image = d.getVar('DM_VERITY_IMAGE')
    verity_type = d.getVar('DM_VERITY_IMAGE_TYPE')
    image_fstypes = d.getVar('IMAGE_FSTYPES')
    pn = d.getVar('PN')

    if verity_image != pn:
        return # This doesn't concern this image

    if not verity_image or not verity_type:
        bb.warn('dm-verity-img class inherited but not used')
        return

    if len(verity_type.split()) is not 1:
        bb.fatal('DM_VERITY_IMAGE_TYPE must contain exactly one type')

    d.appendVar('IMAGE_FSTYPES', ' %s.verity' % verity_type)

    # If we're using wic: we'll have to use partition images and not the rootfs
    # source plugin so add the appropriate dependency.
    if 'wic' in image_fstypes:
        dep = ' %s:do_image_%s' % (pn, verity_type)
        d.appendVarFlag('do_image_wic', 'depends', dep)
}
