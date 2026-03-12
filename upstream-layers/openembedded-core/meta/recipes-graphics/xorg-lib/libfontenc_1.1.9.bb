SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xorgproto font-util"
PE = "1"

SRC_URI[sha256sum] = "9d8392705cb10803d5fe1d27d236cbab3f664e26841ce01916bbbe430cf273e2"

BBCLASSEXTEND = "native"
