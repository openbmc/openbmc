HOMEPAGE = "https://github.com/open-power"
LICENSE = "Apache-2.0"

SRC_URI += "git://github.com/open-power/skiboot.git;branch=skiboot-6.5.x"

FILESEXTRAPATHS_prepend := "${THISDIR}/skiboot:"

LIC_FILES_CHKSUM = "file://${S}/LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "aff9301bb341b5387228e82036a7d72e519d8855"
PV = "6.5.1+git${SRCPV}"

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

# libflash.so is a SONAME and is strongly required in the production
INSANE_SKIP_${PN}_append = "dev-so"
FILES_SOLIBSDEV = ""
FILES_${PN}_append = " ${libdir}/libflash.so"
