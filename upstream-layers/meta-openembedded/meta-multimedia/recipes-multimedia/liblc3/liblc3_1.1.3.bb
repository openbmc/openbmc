SUMMARY = "Low Complexity Communication Codec (LC3)"
HOMEPAGE = "https://github.com/google/liblc3"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/liblc3.git;protocol=https;branch=main;tag=v${PV}"

SRCREV = "96a3af0beb5487aca3b98a4b992a539a1f6d80d1"

inherit pkgconfig meson

TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"

BBCLASSEXTEND = "native nativesdk"

