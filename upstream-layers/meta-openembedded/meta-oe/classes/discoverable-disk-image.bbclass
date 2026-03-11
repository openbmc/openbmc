##
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
#
# Discoverable Disk Image (DDI)
#
# "DDIs (Discoverable Disk Images) are self-describing file system
# images that follow the DPS ( Discoverable Partitions Specification),
# wrapped in a GPT partition table, that may contain root (or /usr/)
# filesystems for bootable OS images, system extensions, configuration
# extensions, portable services, containers and more, and shall be
# protected by signed dm-verity all combined into one. They are
# designed to be composable and stackable, and provide security by
# default."
# https://uapi-group.org/specifications/specs/discoverable_disk_image/
# https://uapi-group.org/specifications/specs/discoverable_partitions_specification/
# https://www.freedesktop.org/software/systemd/man/latest/systemd.image-policy.html

# To be able to use discoverable-disk-images with a
# root-verity-sig or usr-verity-sig configuration:
# - systemd needs to include the PACKAGECONFIG 'cryptsetup', and
# - the kernel needs the following features enabled:
#   CONFIG_DM_VERITY_VERIFY_ROOTHASH_SIG=y
#   CONFIG_DM_VERITY_VERIFY_ROOTHASH_SIG_PLATFORM_KEYRING=y
#   CONFIG_EROFS_FS=y
#   CONFIG_EROFS_FS_XATTR=y
#   CONFIG_EROFS_FS_ZIP=y
#   CONFIG_EROFS_FS_ZIP_LZMA=y
#   CONFIG_INTEGRITY_SIGNATURE=y
#   CONFIG_INTEGRITY_ASYMMETRIC_KEYS=y
#   CONFIG_INTEGRITY_PLATFORM_KEYRING=y
#   CONFIG_SYSTEM_BLACKLIST_KEYRING=y
#   CONFIG_SYSTEM_BLACKLIST_HASH_LIST=""
#   CONFIG_SIGNATURE=y

# To sign DDIs, a key and certificate need to be provided by setting
# the variables:
#   REPART_PRIVATE_KEY
#     private key so sign the verity-hash
#   REPART_PRIVATE_KEY_SOURCE
#     optional, can be "engine:pkcs11" when using a (soft)hsm
#   REPART_CERTIFICATE
#     corresponding public certificate, in .pem format
#

# For signature verification, systemd-sysext expects the matching
# certificate to reside in /etc/verity.d as PEM formated .crt file.
#
# To enforce loading of only signed extension images, an appropriate
# image policy has to be passed to systemd-sysext, e.g.:
# systemd-sysext --image-policy='root=signed+absent:usr=signed+absent:=unused+absent' merge

# 'systemd-dissect' can be used to inspect, manually mount, ... a DDI.

inherit image

IMAGE_FSTYPES = "ddi"

DEPENDS += " \
    systemd-repart-native \
    erofs-utils-native \
    openssl-native \
"

# systemd-repart --make-ddi takes one of "sysext", "confext" or "portable",
# which it then takes and looks up definitions in the host os; which we need
# to divert to the sysroot-native by setting '--definitions=' instead.
# The chosen DDI_TYPE influences which parts of the rootfs are copied into
# the ddi by systemd-repart:
#   sysext: /usr (and if it exists: /opt)
#   confext: /etc
#   portable: /
# For details see systemd/repart/definitions/${REPART_DDI_TYPE}.repart.d/*
REPART_DDI_TYPE ?= "sysext"

REPART_DDI_EXTENSION ?= "ddi"

# systemd-repart creates temporary directoryies under /var/tmp/.#repartXXXXXXX/,
# to estimate partition size etc. Since files are copied there from the image/rootfs
# folder - which are owned by pseudo-root - this temporary location has to be
# added to the directories handled by pseudo; otherwise calls to e.g.
# fchown(0,0) inside systemd git/src/shared/copy.c end up failing.
PSEUDO_INCLUDE_PATHS .= ",/var/tmp/"

oe_image_systemd_repart_make_ddi() {

    local additional_args=""

    if [ -n "${REPART_PRIVATE_KEY}" ]
    then
        if [ -n "${REPART_PRIVATE_KEY_SOURCE}" ]
        then
            additional_args="$additional_args --private-key-source=${REPART_PRIVATE_KEY_SOURCE}"
        fi
        additional_args="$additional_args --private-key=${REPART_PRIVATE_KEY}"
    fi

    if [ -n "${REPART_CERTIFICATE}" ]
    then
        additional_args="$additional_args --certificate=${REPART_CERTIFICATE}"
    fi

    # map architectures to systemd's expected values
    local systemd_arch="${TARGET_ARCH}"
    case "${systemd_arch}" in
        aarch64)
        systemd_arch=arm64
        ;;
        x86_64)
        systemd_arch=x86-64
        ;;
    esac

    # prepare system-repart configuration
    mkdir -p ${B}/definitions.repart.d
    cp ${STAGING_LIBDIR_NATIVE}/systemd/repart/definitions/${REPART_DDI_TYPE}.repart.d/* ${B}/definitions.repart.d/
    # enable erofs compression
    sed -i "/^Compression/d" ${B}/definitions.repart.d/10-root.conf
    echo "Compression=lzma\nCompressionLevel=3" >> ${B}/definitions.repart.d/10-root.conf
    # disable verity signature partition creation, if no key is provided
    if [ -z "${REPART_PRIVATE_KEY}" ]; then
        rm ${B}/definitions.repart.d/30-root-verity-sig.conf
    fi

    systemd-repart \
        --definitions="${B}/definitions.repart.d/" \
        --copy-source="${IMAGE_ROOTFS}" \
        --empty=create --size=auto --dry-run=no --offline=yes \
        --architecture="${systemd_arch}" \
        --json=pretty --no-pager $additional_args \
        "${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${REPART_DDI_EXTENSION}"
}

IMAGE_CMD:ddi = "oe_image_systemd_repart_make_ddi"
do_image_ddi[deptask] += "do_unpack"
