SUMMARY = "Set of i2c tools for linux"
HOMEPAGE = "https://i2c.wiki.kernel.org/index.php/I2C_Tools"
DESCRIPTION = "The i2c-tools package contains a heterogeneous set of I2C tools for Linux: a bus probing tool, a chip dumper, register-level SMBus access helpers, EEPROM decoding scripts, EEPROM programming tools, and a python module for SMBus access. All versions of Linux are supported, as long as I2C support is included in the kernel."
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/${BP}.tar.gz \
"

SRC_URI[md5sum] = "3536237a6b51fb10caacdc3b8a496237"
SRC_URI[sha256sum] = "ef8f77afc70e7dbfd1171bfeae87a8a7f10074829370ce8d9ccd585a014e0073"

inherit update-alternatives

EXTRA_OEMAKE = "bindir=${bindir} sbindir=${sbindir} \
                incdir=${includedir} libdir=${libdir} \
                mandir=${mandir} \
                EXTRA=eeprog"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

PACKAGES =+ "${PN}-misc"
FILES_${PN}-misc = "${sbindir}/i2c-stub-from-dump \
                        ${bindir}/ddcmon \
                        ${bindir}/decode-edid \
                        ${bindir}/decode-dimms \
                        ${bindir}/decode-vaio \
                       "
RDEPENDS_${PN}-misc = "${PN} perl perl-module-posix \
                       perl-module-constant perl-module-file-basename \
                       perl-module-fcntl perl-module-strict perl-module-vars \
		       perl-module-carp \
                      "

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "i2cdetect i2cdump i2cget i2cset i2ctransfer"
ALTERNATIVE_LINK_NAME[i2cdetect] = "${sbindir}/i2cdetect"
ALTERNATIVE_LINK_NAME[i2cdump] = "${sbindir}/i2cdump"
ALTERNATIVE_LINK_NAME[i2cget] = "${sbindir}/i2cget"
ALTERNATIVE_LINK_NAME[i2cset] = "${sbindir}/i2cset"
ALTERNATIVE_LINK_NAME[i2ctransfer] = "${sbindir}/i2ctransfer"
