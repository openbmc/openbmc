SUMMARY = "nmon performance monitor"
HOMEPAGE = "http://nmon.sf.net"
SECTION = "console/utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${WORKDIR}/Documentation.txt;md5=dbb13658cf55d687c4f2ff771a696d4a"
DEPENDS = "ncurses"
PR = "r2"

SRC_URI = "${SOURCEFORGE_MIRROR}/nmon/lmon13g.c;name=lmon \
           ${SOURCEFORGE_MIRROR}/nmon/Documentation.txt;name=doc \
"

SRC_URI[lmon.md5sum] = "b1b8e6c0123ad232394991f2d4f40494"
SRC_URI[lmon.sha256sum] = "456ab2a342b31d1a352d0d940af5962fa65a12ae8757ff73e6e73210832ae8b5"
SRC_URI[doc.md5sum] = "dbb13658cf55d687c4f2ff771a696d4a"
SRC_URI[doc.sha256sum] = "1f7f83afe62a7210be5e83cd24157adb854c14599efe0b377a7ecca933869278"

CFLAGS += "-D JFS -D GETUSER -Wall -D LARGEMEM"
LDFLAGS += "-ltinfo -lncursesw"
ASNEEDED_pn-nmon = ""

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} ${WORKDIR}/lmon13g.c -o nmon
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 nmon ${D}${bindir}
}
