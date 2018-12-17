SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xorgproto font-util"
PE = "1"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "0920924c3a9ebc1265517bdd2f9fde50"
SRC_URI[sha256sum] = "70588930e6fc9542ff38e0884778fbc6e6febf21adbab92fd8f524fe60aefd21"
