LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://file1 \
           file://file2"

SRC_URI:append:class-native = " file://file3"

S = "${UNPACKDIR}"

EXCLUDE_FROM_WORLD = "1"
BBCLASSEXTEND = "native"
