SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xorgproto font-util"
PE = "1"

SRC_URI[sha256sum] = "7b02c3d405236e0d86806b1de9d6868fe60c313628b38350b032914aa4fd14c6"

BBCLASSEXTEND = "native"
