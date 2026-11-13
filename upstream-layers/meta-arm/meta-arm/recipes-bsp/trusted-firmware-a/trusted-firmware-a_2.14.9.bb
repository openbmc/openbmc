require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.14.9
SRC_URI_TRUSTED_FIRMWARE_A = "gitsm://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRCREV_tfa = "16868ce4eafdbf7903fcd7eb15429d4e8b258de4"
SRCBRANCH = "lts-v2.14"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

SRC_URI += "file://0001-feat-build-add-HOSTLDFLAGS-to-pass-flags-to-host-lin.patch"
