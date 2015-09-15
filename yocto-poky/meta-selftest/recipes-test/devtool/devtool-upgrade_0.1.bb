#
# This file was derived from the 'Hello World!' example recipe in the
# Yocto Project Development Manual.
#

DESCRIPTION = "Simple helloworld application used to test the devtool upgrade feature"
SECTION = "devtool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
PR = "r0"

SRC_URI = "file://${THISDIR}/files/${P}.tar.gz \
           file://0001-helloword.c-exit-with-EXIT_SUCCESS-instead-of-a-magi.patch \
           "

S = "${WORKDIR}/${P}"

do_compile() {
	     ${CC} helloworld.c -o helloworld
}

do_install() {
	     install -d ${D}${bindir}
	     install -m 0755 helloworld ${D}${bindir}
}
