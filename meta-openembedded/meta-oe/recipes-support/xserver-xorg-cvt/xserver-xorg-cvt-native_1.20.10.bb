SUMMARY = "X.Org X cvt"
HOMEPAGE = "https://linux.die.net/man/1/cvt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df87950af51ac2c5822094553ea1880"

DEPENDS += "pixman-native xorgproto-native libxrandr-native"

XORG_PN = "xorg-server"

SRC_URI = "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${PV}.tar.bz2"
SRC_URI[sha256sum] = "977420c082450dc808de301ef56af4856d653eea71519a973c3490a780cb7c99"

S = "${WORKDIR}/${XORG_PN}-${PV}"
B = "${WORKDIR}/build"

inherit pkgconfig native

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
