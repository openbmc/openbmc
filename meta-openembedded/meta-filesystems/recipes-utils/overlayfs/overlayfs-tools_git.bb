DESCRIPTION = "Maintenance tools for OverlayFS"
HOMEPAGE = "https://github.com/kmxz/overlayfs-tools"
LICENSE = "WTFPL"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f312a7c4d02230e8f2b537295d375c69"

SRC_URI = "\
    git://github.com/kmxz/overlayfs-tools.git;protocol=https;branch=master \
"

PV = "1.0+git${SRCPV}"
SRCREV = "b5e5a829895ac98ccfe4629fbfbd8b819262bd00"

S = "${WORKDIR}/git"
B = "${S}"

# Required to have the fts.h header for musl
DEPENDS:append:libc-musl = " fts"

EXTRA_OEMAKE += "'CC=${CC} -O2'"
# Fix the missing fts libs when using musl
EXTRA_OEMAKE:append:libc-musl = " LDLIBS=-lfts"
TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/overlay ${D}${bindir}
    install -m 0755 ${B}/fsck.overlay ${D}${bindir}
}
