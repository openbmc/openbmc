SUMMARY = "nmon performance monitor"
HOMEPAGE = "http://nmon.sf.net"
SECTION = "console/utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${WORKDIR}/Documentation.txt;md5=dbb13658cf55d687c4f2ff771a696d4a"
DEPENDS = "ncurses"
DEPENDS_append_libc-musl = " bsd-headers"

SRC_URI = "${SOURCEFORGE_MIRROR}/nmon/lmon${PV}.c;name=lmon \
           ${SOURCEFORGE_MIRROR}/nmon/Documentation.txt;name=doc \
           file://0001-lmon16g.c-Adjust-system-headers.patch \
"
SRC_URI[lmon.md5sum] = "246ccfc74d5af55d992601fc4d3d4a72"
SRC_URI[lmon.sha256sum] = "da82dd693b503b062854dfe7dbb5d36b347872ab44a4aa05b97e9d577747f688"
SRC_URI[doc.md5sum] = "dbb13658cf55d687c4f2ff771a696d4a"
SRC_URI[doc.sha256sum] = "1f7f83afe62a7210be5e83cd24157adb854c14599efe0b377a7ecca933869278"

CFLAGS += "-D JFS -D GETUSER -Wall -D LARGEMEM"
LDFLAGS += "-ltinfo -lncursesw -lm"
ASNEEDED_pn-nmon = ""

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} lmon${PV}.c -o nmon
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 nmon ${D}${bindir}
}
