SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xorgproto font-util"
PE = "1"

XORG_EXT = "tar.xz"

SRC_URI[sha256sum] = "ea8606ed5255dda8f570b7d1a74d59ee8d198675b2f114d07807431e6ba1d111"

BBCLASSEXTEND = "native"
