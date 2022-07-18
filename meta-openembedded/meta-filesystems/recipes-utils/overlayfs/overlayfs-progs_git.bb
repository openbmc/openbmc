SUMMARY = "File system check utility for OverlayFS"
HOMEPAGE = "https://github.com/hisilicon/overlayfs-progs"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/PD;md5=b3597d12946881e13cb3b548d1173851"

SRC_URI = "\
    git://github.com/hisilicon/overlayfs-progs.git;protocol=https;branch=master \
    file://0001-Makefile-proper-location-of-LDFLAGS.patch \
"

PV = "1.0+git${SRCPV}"
SRCREV = "e10ef686570d9c7eff42f52461593a5c15da56bd"

S = "${WORKDIR}/git"
B = "${S}"

# Required to have the fts.h header for musl
DEPENDS:append:libc-musl = " fts"
# Fix the missing fts libs when using musl
EXTRA_OEMAKE:append:libc-musl = " LDFLAGS='-lfts'"

EXTRA_OEMAKE += "'CC=${CC} -O2' "
TARGET_CC_ARCH += "${LDFLAGS}"

do_compile () {
    oe_runmake
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/fsck.overlay ${D}${bindir}
}
