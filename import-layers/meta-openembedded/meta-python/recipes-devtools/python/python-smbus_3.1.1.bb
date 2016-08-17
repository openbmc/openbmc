SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://smbusmodule.c;startline=1;endline=17;md5=fa24df321a520ff8e10f203425ab9fa8"

SRC_URI = "http://dl.lm-sensors.org/i2c-tools/releases/i2c-tools-${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "0fdbff53ebd0b8d9249256d6c56480b1"
SRC_URI[sha256sum] = "14d4d7d60d1c12e43f2befe239c682a5c44c27682f153d4b58c1e392d2db1700"

DEPENDS = "i2c-tools"

inherit distutils

S = "${WORKDIR}/i2c-tools-${PV}/py-smbus/"

do_configure_prepend() {
    # Adjust for OE header rename
    sed -i s:linux/i2c-dev.h:linux/i2c-dev-user.h: Module.mk
    sed -i s:linux/i2c-dev.h:linux/i2c-dev-user.h: smbusmodule.c
}
