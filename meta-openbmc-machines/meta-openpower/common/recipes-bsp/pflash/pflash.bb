SUMMARY = "pflash programmer for OpenPower"
DESCRIPTION = "pflash firmware programming tool for OpenPower machines"
HOMEPAGE = "https://github.com/open-power"
LICENSE = "Apache-2.0"

SRC_URI += "git://github.com/open-power/skiboot.git"
LIC_FILES_CHKSUM = "file://${S}/LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "skiboot-5.2.0"
PV = "5.2.0"
SRC_URI += "file://0001-pflash-Allow-building-under-yocto.patch"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} PFLASH_VERSION=${PV} V=1'

do_compile () {
        oe_runmake -C external/pflash all
}

do_install () {
        oe_runmake -C external/pflash install DESTDIR=${D}
}
