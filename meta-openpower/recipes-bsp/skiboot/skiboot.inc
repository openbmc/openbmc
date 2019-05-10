HOMEPAGE = "https://github.com/open-power"
LICENSE = "Apache-2.0"

SRC_URI += "git://github.com/open-power/skiboot.git"

FILESEXTRAPATHS_prepend := "${THISDIR}/skiboot:"

LIC_FILES_CHKSUM = "file://${S}/LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "54811997b6d02dacaf8dd0d61cadaf3786dbabb0"
PV = "6.3.0+git${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} SKIBOOT_VERSION=${PV} V=1"

SKIBOOT_SUBDIR ?= "external/${PN}"

do_compile() {
    oe_runmake -C ${SKIBOOT_SUBDIR} all
}

# TODO: remove PREFIX=$prefix when updating to a version that has
# http://patchwork.ozlabs.org/patch/923741/ is merged upstream
do_install() {
    oe_runmake -C ${SKIBOOT_SUBDIR} install DESTDIR=${D} prefix=/usr PREFIX=$prefix
}
