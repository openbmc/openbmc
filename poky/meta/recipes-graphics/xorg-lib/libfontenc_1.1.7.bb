SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xorgproto font-util"
PE = "1"

SRC_URI[sha256sum] = "c0d36991faee06551ddbaf5d99266e97becdc05edfae87a833c3ff7bf73cfec2"

BBCLASSEXTEND = "native"
