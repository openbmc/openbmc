require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A integration branch, current commit
SRC_URI_TRUSTED_FIRMWARE_A = "gitsm://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRCREV_tfa = "abead3308de3eb2d89a5ce3fe264ea829f87648c"
SRCBRANCH = "integration"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
UPSTREAM_CHECK_COMMITS = "1"
