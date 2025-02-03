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
# Optionally, we can store the hash data on a separate device or partition
# for improved compartmentalization and ease of use/deployment.
#
# Usage:
#     DM_VERITY_IMAGE = "core-image-full-cmdline" # or other image
#     DM_VERITY_IMAGE_TYPE = "ext4" # or ext2, ext3 & btrfs
#     DM_VERITY_SEPARATE_HASH = "1" # optional; store hash on separate dev
#     IMAGE_CLASSES += "dm-verity-img"
#
# Using the GPT UUIDs specified in the standard can also be useful in that
# they are displayed and translated in cfdisk output.
#
#     DM_VERITY_ROOT_GUID = <UUID for your architecture and root-fs>
#     DM_VERITY_RHASH_GUID = <UUID for your architecture and verity-hash>
# https://uapi-group.org/specifications/specs/discoverable_partitions_specification/

# The resulting image can then be used to implement the device mapper block
# integrity checking on the target device.

# Define the location where the DM_VERITY_IMAGE specific dm-verity root hash
# is stored where it can be installed into associated initramfs rootfs.
STAGING_VERITY_DIR ?= "${TMPDIR}/work-shared/${MACHINE}/dm-verity"

# location of images, default current image recipe. Set to DEPLOY_DIR_IMAGE
# if non-verity images want to embed the .wks and verity image.
DM_VERITY_DEPLOY_DIR ?= "${IMGDEPLOYDIR}"

# Define the data block size to use in veritysetup.
DM_VERITY_IMAGE_DATA_BLOCK_SIZE ?= "1024"

# Define the hash block size to use in veritysetup.
DM_VERITY_IMAGE_HASH_BLOCK_SIZE ?= "4096"

# Should we store the hash data on a separate device/partition?
DM_VERITY_SEPARATE_HASH ?= "0"

# Additional arguments for veritysetup
DM_VERITY_SETUP_ARGS ?= ""

# These are arch specific.  We could probably intelligently auto-assign these?
# Take x86-64 values as defaults. No impact on functionality currently.
# See SD_GPT_ROOT_X86_64 and SD_GPT_ROOT_X86_64_VERITY in the spec.
# Note - these are passed directly to sgdisk so hyphens needed.
DM_VERITY_ROOT_GUID ?= "4f68bce3-e8cd-4db1-96e7-fbcaf984b709"
DM_VERITY_RHASH_GUID ?= "2c7357ed-ebd2-46d9-aec1-23d437ec2bf5"

DEPENDS += "bc-native"

# Process the output from veritysetup and generate the corresponding .env
# file. The output from veritysetup is not very machine-friendly so we need to
# convert it to some better format. Let's drop the first line (doesn't contain
# any useful info) and feed the rest to a script.
process_verity() {
    local ENV="${STAGING_VERITY_DIR}/${DM_VERITY_IMAGE}.$TYPE.verity.env"
    local WKS_INC="${STAGING_VERITY_DIR}/${DM_VERITY_IMAGE}.$TYPE.wks.in"
    rm -f $ENV

    # Each line contains a key and a value string delimited by ':'. Read the
    # two parts into separate variables and process them separately. For the
    # key part: convert the names to upper case and replace spaces with
    # underscores to create correct shell variable names. For the value part:
    # just trim all white-spaces.
    IFS=":"
    while read KEY VAL; do
        printf '%s=%s\n' \
            "$(echo "$KEY" | tr '[:lower:]' '[:upper:]' | sed 's/ /_/g')" \
            "$(echo "$VAL" | tr -d ' \t')" >> $ENV
    done

    # Add partition size
    echo "DATA_SIZE=$SIZE" >> $ENV

    # Add whether we are storing the hash data separately
    echo "SEPARATE_HASH=${DM_VERITY_SEPARATE_HASH}" >> $ENV

    # Configured for single partition use of veritysetup?  OK, we are done.
    if [ ${DM_VERITY_SEPARATE_HASH} -eq 0 ]; then
        return
    fi

    # Craft up the UUIDs that are part of the verity standard for root & hash
    # while we are here and in shell.  Re-read our output to get ROOT_HASH
    # and then cut it in 1/2 ; HI for data UUID and LO for hash-data UUID.
    # https://uapi-group.org/specifications/specs/discoverable_partitions_specification/

    ROOT_HASH=$(cat $ENV | grep ^ROOT_HASH | sed 's/ROOT_HASH=//' | tr a-f A-F)
    ROOT_HI=$(echo "obase=16;ibase=16;$ROOT_HASH/2^80" | bc)
    ROOT_LO=$(echo "obase=16;ibase=16;$ROOT_HASH%2^80" | bc)

    # Hyphenate as per UUID spec and as expected by wic+sgdisk parameters.
    # Prefix with leading zeros, in case hash chunks weren't using highest bits
    # "bc" needs upper case, /dev/disk/by-partuuid/ is lower case. <sigh>
    ROOT_UUID=$(echo 00000000$ROOT_HI | sed 's/.*\(.\{32\}\)$/\1/' | \
        sed 's/./-&/9;s/./-&/14;s/./-&/19;s/./-&/24' | tr A-F a-f )
    RHASH_UUID=$(echo 00000000$ROOT_LO | sed 's/.*\(.\{32\}\)$/\1/' | \
        sed 's/./-&/9;s/./-&/14;s/./-&/19;s/./-&/24' | tr A-F a-f )

    # Emit the values needed for a veritysetup run in the initramfs
    echo "ROOT_UUID=$ROOT_UUID" >> $ENV
    echo "RHASH_UUID=$RHASH_UUID" >> $ENV

    # Create wks.in fragment with build specific UUIDs for partitions.
    # Unfortunately the wks.in does not support line continuations...
    # First, the unappended filesystem data partition.
    echo 'part / --source rawcopy --ondisk sda --sourceparams="file=${DM_VERITY_DEPLOY_DIR}/${DM_VERITY_IMAGE}-${MACHINE}${IMAGE_NAME_SUFFIX}.${DM_VERITY_IMAGE_TYPE}.verity" --part-name verityroot --part-type="${DM_VERITY_ROOT_GUID}"'" --uuid=\"$ROOT_UUID\"" > $WKS_INC

    # note: no default mount point for hash data partition
    echo 'part --source rawcopy --ondisk sda --sourceparams="file=${DM_VERITY_DEPLOY_DIR}/${DM_VERITY_IMAGE}-${MACHINE}${IMAGE_NAME_SUFFIX}.${DM_VERITY_IMAGE_TYPE}.vhash" --part-name verityhash --part-type="${DM_VERITY_RHASH_GUID}"'" --uuid=\"$RHASH_UUID\"" >> $WKS_INC
}

verity_setup() {
    local TYPE=$1
    local INPUT=${IMAGE_NAME}.$TYPE
    local SIZE=$(stat --printf="%s" $INPUT)
    local OUTPUT=$INPUT.verity
    local OUTPUT_HASH=$INPUT.verity
    local HASH_OFFSET=""
    local SETUP_ARGS=""
    local SAVED_ARGS="${STAGING_VERITY_DIR}/${IMAGE_BASENAME}.$TYPE.verity.args"

    install -d ${STAGING_VERITY_DIR}

    if [ ${DM_VERITY_IMAGE_DATA_BLOCK_SIZE} -ge ${DM_VERITY_IMAGE_HASH_BLOCK_SIZE} ]; then
        align=${DM_VERITY_IMAGE_DATA_BLOCK_SIZE}
    else
        align=${DM_VERITY_IMAGE_HASH_BLOCK_SIZE}
    fi
    SIZE=$(expr \( $SIZE + $align - 1 \) / $align \* $align)

    # Assume some users may want separate hash vs. appended hash
    if [ ${DM_VERITY_SEPARATE_HASH} -eq 1 ]; then
        OUTPUT_HASH=$INPUT.vhash
    else
        HASH_OFFSET="--hash-offset="$SIZE
    fi

    cp -a $INPUT $OUTPUT

    SETUP_ARGS=" \
        ${DM_VERITY_SETUP_ARGS} \
        --data-block-size=${DM_VERITY_IMAGE_DATA_BLOCK_SIZE} \
        --hash-block-size=${DM_VERITY_IMAGE_HASH_BLOCK_SIZE} \
        $HASH_OFFSET format $OUTPUT $OUTPUT_HASH \
    "

    echo "veritysetup $SETUP_ARGS" > $SAVED_ARGS

    # Let's drop the first line of output (doesn't contain any useful info)
    # and feed the rest to another function.
    veritysetup $SETUP_ARGS | tail -n +2 | process_verity
}

# make "dateless" symlink for the hash so the wks can find it.
verity_hash() {
    cd ${IMGDEPLOYDIR}
    ln -sf ${IMAGE_NAME}.${DM_VERITY_IMAGE_TYPE}.vhash \
        ${IMAGE_BASENAME}-${MACHINE}${IMAGE_NAME_SUFFIX}.${DM_VERITY_IMAGE_TYPE}.vhash
}

VERITY_TYPES = " \
    ext2.verity ext3.verity ext4.verity \
    btrfs.verity \
    erofs.verity erofs-lz4.verity erofs-lz4hc.verity \
    squashfs.verity squashfs-xz.verity squashfs-lzo.verity squashfs-lz4.verity squashfs-zst.verity \
"
IMAGE_TYPES += "${VERITY_TYPES}"
CONVERSIONTYPES += "verity"
CONVERSION_CMD:verity = "verity_setup ${type}"
CONVERSION_DEPENDS_verity = "cryptsetup-native"
IMAGE_CMD:vhash = "verity_hash"

def get_verity_fstypes(d):
    verity_image = d.getVar('DM_VERITY_IMAGE')
    verity_type = d.getVar('DM_VERITY_IMAGE_TYPE')
    verity_hash = d.getVar('DM_VERITY_SEPARATE_HASH')
    pn = d.getVar('PN')

    fstypes = ""
    if not pn.endswith(verity_image):
        return fstypes # This doesn't concern this image

    fstypes = verity_type + ".verity"
    if verity_hash == "1":
        fstypes += " vhash"

    return fstypes

IMAGE_FSTYPES += "${@get_verity_fstypes(d)}"

python __anonymous() {
    verity_image = d.getVar('DM_VERITY_IMAGE')
    verity_type = d.getVar('DM_VERITY_IMAGE_TYPE')
    verity_hash = d.getVar('DM_VERITY_SEPARATE_HASH')
    image_fstypes = d.getVar('IMAGE_FSTYPES')
    pn = d.getVar('PN')

    if not verity_image or not verity_type:
        bb.warn('dm-verity-img class inherited but not used')
        return

    if not pn.endswith(verity_image):
        return # This doesn't concern this image

    if len(verity_type.split()) != 1:
        bb.fatal('DM_VERITY_IMAGE_TYPE must contain exactly one type')

    # If we're using wic: we'll have to use partition images and not the rootfs
    # source plugin so add the appropriate dependency.
    if 'wic' in image_fstypes:
        dep = ' %s:do_image_%s' % (pn, verity_type.replace("-", "_"))
        d.appendVarFlag('do_image_wic', 'depends', dep)
}
