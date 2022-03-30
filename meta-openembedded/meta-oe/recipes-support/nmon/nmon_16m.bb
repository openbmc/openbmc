SUMMARY = "nmon performance monitor"
HOMEPAGE = "http://nmon.sf.net"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://Documentation.txt;md5=dbb13658cf55d687c4f2ff771a696d4a"
DEPENDS = "ncurses"
DEPENDS:append:libc-musl = " bsd-headers"

SRC_URI = "${SOURCEFORGE_MIRROR}/nmon/lmon${PV}.c;name=lmon \
           ${SOURCEFORGE_MIRROR}/nmon/Documentation.txt;name=doc \
           file://0001-lmon16g.c-Adjust-system-headers.patch \
           file://0001-Fix-a-lot-of-Werror-format-security-errors-with-mvpr.patch \
           "
SRC_URI[lmon.sha256sum] = "2bed4d45fdfdf1d1387ec91e139c04975d5f838e3e0d53c0fe2d803a707e5fc1"
SRC_URI[doc.sha256sum] = "1f7f83afe62a7210be5e83cd24157adb854c14599efe0b377a7ecca933869278"

CFLAGS += "-D JFS -D GETUSER -Wall -D LARGEMEM"
LDFLAGS += "-ltinfo -lncursesw -lm"
ASNEEDED:pn-nmon = ""

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} lmon${PV}.c -o nmon
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 nmon ${D}${bindir}
}
