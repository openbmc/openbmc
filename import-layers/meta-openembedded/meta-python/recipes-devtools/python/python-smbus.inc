SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://smbusmodule.c;beginline=1;endline=17;md5=fa24df321a520ff8e10f203425ab9fa8"
PR = "r1"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/i2c-tools-${PV}.tar.gz "
SRC_URI[md5sum] = "d92a288d70f306d3895e3a7e9c14c9aa"
SRC_URI[sha256sum] = "5b60daf6f011de0acb61de57dba62f2054bb39f19961d67e0c91610f071ca403"

DEPENDS += "i2c-tools"

S = "${WORKDIR}/i2c-tools-${PV}/py-smbus/"
