SUMMARY = "X.Org X cvt"
HOMEPAGE = "https://linux.die.net/man/1/cvt"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df87950af51ac2c5822094553ea1880"

DEPENDS += "pixman-native xorgproto-native libxrandr-native"

XORG_PN = "xorg-server"

SRC_URI = "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "c9fc7e21e11286dbedd22c00df652130"
SRC_URI[sha256sum] = "a81d8243f37e75a03d4f8c55f96d0bc25802be6ec45c3bfa5cb614c6d01bac9d"

S = "${WORKDIR}/${XORG_PN}-${PV}"
B = "${WORKDIR}/build"

inherit native pkgconfig

do_configure[noexec] = "1"

do_compile() {
    cd ${S}
    for header in `find -name '*.h'`; do
        path=`dirname $header`
        if ! echo "$incpaths" | grep -q "$path" ; then
            incpaths="$incpaths -I$path"
        fi
    done
    CFLAGS="${CFLAGS} -DXORG_VERSION_CURRENT=1 $incpaths `pkg-config --cflags pixman-1`"
    LDFLAGS="${LDFLAGS} -lm `pkg-config --libs pixman-1`"
    ${CC} $CFLAGS -o ${B}/cvt \
        ${S}/hw/xfree86/utils/cvt/cvt.c \
        ${S}/hw/xfree86/modes/xf86cvt.c \
        ${S}/os/xprintf.c \
        $LDFLAGS
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${B}/cvt ${D}${bindir}
}
