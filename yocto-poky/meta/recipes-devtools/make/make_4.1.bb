LICENSE = "GPLv3 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://tests/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://glob/COPYING.LIB;md5=4a770b67e6be0f60da244beb2de0fce4"
require make.inc

SRC_URI += "file://0001-main.c-main-SV-43434-Handle-NULL-returns-from-ttynam.patch"

EXTRA_OECONF += "--without-guile"

SRC_URI[md5sum] = "57a7a224a822f94789a587ccbcedff69"
SRC_URI[sha256sum] = "0bc7613389650ee6a24554b52572a272f7356164fd2c4132b0bcf13123e4fca5"

BBCLASSEXTEND = "native nativesdk"
