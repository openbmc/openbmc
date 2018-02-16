HOMEPAGE = "https://github.com/open-power"
LICENSE = "Apache-2.0"

SRC_URI += "git://github.com/open-power/skiboot.git;nobranch=1"
SRC_URI += "file://0001-external-Use-more-standard-PREFIX-vs-prefix.patch"

FILESEXTRAPATHS_append := "${THISDIR}/skiboot:"

LIC_FILES_CHKSUM = "file://${S}/LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "1d7da2e9b05b4995690abda54f853e0d84833af8"
PV = "v5.10-rc3"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} SKIBOOT_VERSION=${PV} V=1"

SKIBOOT_SUBDIR ?= "external/${PN}"

do_compile() {
    oe_runmake -C ${SKIBOOT_SUBDIR} all
}

do_install() {
    oe_runmake -C ${SKIBOOT_SUBDIR} install DESTDIR=${D} PREFIX=/usr
}
