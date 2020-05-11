require xorg-app-common.inc

SUMMARY = "X11 server performance test program"

DESCRIPTION = "The x11perf program runs one or more performance tests \
and reports how fast an X server can execute the tests."


DEPENDS += "libxmu libxrender libxft libxext fontconfig"

LIC_FILES_CHKSUM = "file://COPYING;md5=428ca4d67a41fcd4fc3283dce9bbda7e \
                    file://x11perf.h;endline=24;md5=29555066baf406a105ff917ac25b2d01"

PE = "1"

inherit multilib_script

MULTILIB_SCRIPTS = "${PN}:${bindir}/x11perfcomp"

do_install_append_class-target () {
    sed -i -e 's:${HOSTTOOLS_DIR}/::g' ${D}${bindir}/x11perfcomp
}

FILES_${PN} += "${libdir}/X11/x11perfcomp/*"

SRC_URI[md5sum] = "e96b56756990c56c24d2d02c2964456b"
SRC_URI[sha256sum] = "1c7e0b8ffc2794b4ccf11e04d551823abe0ea47b4f7db0637390db6fbe817c34"
