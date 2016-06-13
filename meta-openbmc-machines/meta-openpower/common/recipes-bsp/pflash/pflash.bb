SUMMARY = "pflash programmer for OpenPower"
DESCRIPTION = "pflash firmware programming tool for OpenPower machines"
HOMEPAGE = "https://github.com/open-power"
LICENSE = "Apache-2.0"

SRC_URI += "git://github.com/open-power/skiboot.git"

SRC_URI += "file://0001-Make-links-target-reusable.patch"
SRC_URI += "file://0002-external-Remove-external-shared-link-targets.patch"
SRC_URI += "file://0003-external-Fix-pflash-install-target.patch"
SRC_URI += "file://0004-external-Remove-m64-from-shared-CFLAGS-on-ARM.patch"
SRC_URI += "file://0005-external-Create-shared-rules.mk.patch"
SRC_URI += "file://0006-external-Add-dynamically-linked-pflash.patch"

LIC_FILES_CHKSUM = "file://${S}/LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "skiboot-5.2.2"
PV = "5.2.2"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} PFLASH_VERSION=${PV} V=1'

do_compile () {
        oe_runmake -C external/pflash all LINKAGE=dynamic
}

do_install () {
        oe_runmake -C external/pflash install LINKAGE=dynamic DESTDIR=${D} PREFIX=${D}/usr
}

BBCLASSEXTEND = "native nativesdk"
