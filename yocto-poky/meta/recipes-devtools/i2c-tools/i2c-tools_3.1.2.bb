SUMMARY = "Set of i2c tools for linux"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/${BP}.tar.bz2 \
           file://Module.mk \
"
SRC_URI[md5sum] = "7104a1043d11a5e2c7b131614eb1b962"
SRC_URI[sha256sum] = "db5e69f2e2a6e3aa2ecdfe6a5f490b149c504468770f58921c8c5b8a7860a441"

inherit autotools-brokensep

do_compile_prepend() {
    cp ${WORKDIR}/Module.mk ${S}/eepromer/
    sed -i 's#/usr/local#/usr#' ${S}/Makefile
    echo "include eepromer/Module.mk" >> ${S}/Makefile
}

do_install_append() {
    install -d ${D}${includedir}/linux
    install -m 0644 include/linux/i2c-dev.h ${D}${includedir}/linux/i2c-dev-user.h
    rm -f ${D}${includedir}/linux/i2c-dev.h
}

PACKAGES =+ "${PN}-misc"
FILES_${PN}-misc = "${sbindir}/i2c-stub-from-dump \
                        ${bindir}/ddcmon \
                        ${bindir}/decode-edid \
                        ${bindir}/decode-dimms \
                        ${bindir}/decode-vaio \
                       "
RDEPENDS_${PN}-misc = "${PN} perl"
