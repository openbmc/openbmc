SUMMARY = "Set of i2c tools for linux - Python module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://smbusmodule.c;startline=1;endline=17;md5=fa24df321a520ff8e10f203425ab9fa8"

SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/i2c-tools-${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "7104a1043d11a5e2c7b131614eb1b962"
SRC_URI[sha256sum] = "db5e69f2e2a6e3aa2ecdfe6a5f490b149c504468770f58921c8c5b8a7860a441"

DEPENDS += "i2c-tools"

S = "${WORKDIR}/i2c-tools-${PV}/py-smbus/"

do_configure_prepend() {
    # Adjust for OE header rename
    sed -i s:linux/i2c-dev.h:linux/i2c-dev-user.h: Module.mk
    sed -i s:linux/i2c-dev.h:linux/i2c-dev-user.h: smbusmodule.c
}
