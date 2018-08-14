require xorg-app-common.inc

SUMMARY = "X11 server performance test program"

DESCRIPTION = "The x11perf program runs one or more performance tests \
and reports how fast an X server can execute the tests."


DEPENDS += "libxmu libxrender libxft libxext fontconfig"

LIC_FILES_CHKSUM = "file://COPYING;md5=428ca4d67a41fcd4fc3283dce9bbda7e \
                    file://x11perf.h;endline=24;md5=29555066baf406a105ff917ac25b2d01"

PE = "1"

do_install_append_class-target () {
    sed -i -e 's:${HOSTTOOLS_DIR}/::g' ${D}${bindir}/x11perfcomp
}

FILES_${PN} += "${libdir}/X11/x11perfcomp/*"

SRC_URI[md5sum] = "f0b24e4d8beb622a419e8431e1c03cd7"
SRC_URI[sha256sum] = "e87098dec1947572d70c62697a7b70bde1ab5668237d4660080eade6bc096751"
