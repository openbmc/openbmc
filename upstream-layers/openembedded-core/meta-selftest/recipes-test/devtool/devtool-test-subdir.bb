LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://devtool-test-subdir.tar.gz \
           file://testfile;subdir=${BPN}"

S = "${UNPACKDIR}/${BPN}"

EXCLUDE_FROM_WORLD = "1"
