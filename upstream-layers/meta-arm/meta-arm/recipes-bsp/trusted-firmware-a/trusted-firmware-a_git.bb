require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A master, tag: v2.14
SRCREV_tfa = "1d5aa939bc8d3d892e2ed9945fa50e36a1a924cc"
SRCBRANCH = "master"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.5
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "gitsm://github.com/Mbed-TLS/mbedtls;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "e185d7fd85499c8ce5ca2a54f5cf8fe7dbe3f8df"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
UPSTREAM_CHECK_COMMITS = "1"
