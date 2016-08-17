SUMMARY = "Lightweight and minimal (~20K) dumb-terminal emulation program"
SECTION = "console/utils"
LICENSE = "GPLv2+"
HOMEPAGE = "http://code.google.com/p/picocom/"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "http://picocom.googlecode.com/files/picocom-${PV}.tar.gz"

SRC_URI[md5sum] = "8eaba1d31407e8408674d6e57af447ef"
SRC_URI[sha256sum] = "d0f31c8f7a215a76922d30c81a52b9a2348c89e02a84935517002b3bc2c1129e"

CPPFLAGS_append = '-DVERSION_STR=\\"${PV}\\" -DUUCP_LOCK_DIR=\\"/var/lock\\" -DHIGH_BAUD'

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${BPN} pcasc pcxm pcym pczm ${D}${bindir}/
}

