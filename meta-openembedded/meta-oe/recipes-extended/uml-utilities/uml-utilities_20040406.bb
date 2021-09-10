SECTION = "console/utils"
SUMMARY = "Utilities for User-Mode-Linux"
LICENSE = "GPL-2.0"
DEPENDS = "zlib ncurses readline"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"
SRC_URI = "http://downloads.sourceforge.net/project/user-mode-linux/tools/1/uml_utilities_${PV}.tar.bz2 \
           file://fix-ldflags.patch \
           file://unstrip.patch \
           file://0001-include-required-system-header-files-for-fd_set-and-.patch \
           "
SRC_URI[md5sum] = "2c1ccd9efacbfb39e42d482b89b2550a"
SRC_URI[sha256sum] = "4f179b1db021ef15ac7e9b2eed57c525db127a754c574f591c367460cded9f41"

PR = "r1"

S = "${WORKDIR}/tools"

inherit update-alternatives

do_compile() {
    oe_runmake LIB_DIR=${libdir}/uml
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES:${PN} += "${libdir}/uml"

ALTERNATIVE:${PN} = "tunctl"
ALTERNATIVE_LINK_NAME[tunctl] = "${bindir}/tunctl"
