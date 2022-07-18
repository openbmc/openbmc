DESCRIPTION = "Maintenance tools for OverlayFS"
HOMEPAGE = "https://github.com/kmxz/overlayfs-tools"
LICENSE = "WTFPL"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f312a7c4d02230e8f2b537295d375c69"

SRC_URI = "\
    git://github.com/kmxz/overlayfs-tools.git;protocol=https;branch=master \
    file://0001-Fixed-includes-so-that-it-compiles-on-Ubuntu-20.04.patch \
    file://0002-makefile-fix-linking-flags.patch \
"

PV = "1.0+git${SRCPV}"
SRCREV = "291c7f4a3fb548d06c572700650c2e3bccb0cd27"

S = "${WORKDIR}/git"
B = "${S}"

DEPENDS += "attr"
# Required to have the fts.h header for musl
DEPENDS:append:libc-musl = " fts"

EXTRA_OEMAKE += "'CC=${CC} -O2'"
# Fix the missing fts libs when using musl
EXTRA_OEMAKE:append:libc-musl = " LDLIBS=-lfts"
TARGET_CC_ARCH += "${LDFLAGS}"

do_compile () {
    oe_runmake
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/overlay ${D}${bindir}
}
