SUMMARY = "Set of i2c tools for linux"
HOMEPAGE = "https://i2c.wiki.kernel.org/index.php/I2C_Tools"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/i2c-tools/${BP}.tar.gz \
"

SRC_URI[sha256sum] = "7de18ed890e111fa54ab7ea896804d5faa4d1f0462a258aad9fbb7a8cc6b60cc"

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
