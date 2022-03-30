SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://py-smbus/smbusmodule.c;beginline=1;endline=18;md5=46e424fb045901ab25e0f92c28c80055"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/i2c-tools-${PV}.tar.gz \
           file://0001-Use-setuptools-as-distutils-is-deprecated.patch \
           "
SRC_URI[sha256sum] = "eec464e42301d93586cbeca3845ed61bff40f560670e5b35baec57301d438148"

DEPENDS += "i2c-tools"

S = "${WORKDIR}/i2c-tools-${PV}"

inherit setuptools3

SETUPTOOLS_SETUP_PATH = "${S}/py-smbus"
