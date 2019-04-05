require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=70762511f9c509cc2a4e4ba2ef687ae3"

SRC_URI[md5sum] = "bd6fd69db28426baf22ec0acdd5c4b2a"
SRC_URI[sha256sum] = "a3f2b91a2353b65a863c5901251efe48060ecdebec46b5eaec8ea8e092b9e871"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
            file://0003-Fix-Segmentation-fault-error-when-gcc-o-dev-null.patch \
"
