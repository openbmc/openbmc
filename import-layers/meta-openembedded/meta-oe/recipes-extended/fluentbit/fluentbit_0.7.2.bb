SUMMARY = "Fast data collector for Embedded Linux"
HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"

SRC_URI = "http://fluentbit.io/releases/0.7/fluent-bit-${PV}.tar.gz"
SRC_URI[md5sum] = "6df9d676e1d2d782a243e655e144e8ae"
SRC_URI[sha256sum] = "695b56ce378f56855c9554f88f5d8e4f7b11ba7691284903870f779912af4ebe"
S = "${WORKDIR}/fluent-bit-${PV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

DEPENDS = "zlib"
INSANE_SKIP_${PN}-dev += "dev-elf"

inherit cmake
