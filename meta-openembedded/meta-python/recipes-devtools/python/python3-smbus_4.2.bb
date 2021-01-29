SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://smbusmodule.c;beginline=1;endline=18;md5=46e424fb045901ab25e0f92c28c80055"
PR = "r1"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/i2c-tools-${PV}.tar.gz "
SRC_URI[md5sum] = "d6861c89521f2a2773e19edadb2befee"
SRC_URI[sha256sum] = "7de18ed890e111fa54ab7ea896804d5faa4d1f0462a258aad9fbb7a8cc6b60cc"

DEPENDS += "i2c-tools"

S = "${WORKDIR}/i2c-tools-${PV}/py-smbus"
inherit distutils3
