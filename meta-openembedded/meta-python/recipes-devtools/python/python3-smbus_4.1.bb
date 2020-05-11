SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://smbusmodule.c;beginline=1;endline=18;md5=46e424fb045901ab25e0f92c28c80055"
PR = "r1"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/i2c-tools-${PV}.tar.gz "
SRC_URI[md5sum] = "3536237a6b51fb10caacdc3b8a496237"
SRC_URI[sha256sum] = "ef8f77afc70e7dbfd1171bfeae87a8a7f10074829370ce8d9ccd585a014e0073"

DEPENDS += "i2c-tools"

S = "${WORKDIR}/i2c-tools-${PV}/py-smbus/"
inherit distutils3
