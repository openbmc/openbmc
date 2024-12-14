SECTION = "console/utils"
SUMMARY = "Utilities for User-Mode-Linux"
LICENSE = "GPL-2.0-only"
DEPENDS = "zlib ncurses readline"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"
SRC_URI = "http://downloads.sourceforge.net/project/user-mode-linux/tools/1/uml_utilities_${PV}.tar.bz2 \
           file://fix-ldflags.patch \
           file://unstrip.patch \
           file://0001-include-required-system-header-files-for-fd_set-and-.patch \
           file://0001-Add-missing-standard-headers-for-str-and-exit-APIs.patch \
           file://0001-cow.c-Replace-stat64-with-stat.patch \
           file://0001-makefiles-Append-to-CFLAGS-instead-of-re-assign.patch \
           "
SRC_URI[sha256sum] = "4f179b1db021ef15ac7e9b2eed57c525db127a754c574f591c367460cded9f41"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/user-mode-linux/files/tools/1/"

S = "${WORKDIR}/tools"

inherit update-alternatives

do_compile() {
    oe_runmake LIB_DIR=${libdir}/uml
}

do_install() {
    oe_runmake install DESTDIR=${D} LIB_DIR=${libdir}/uml
}

FILES:${PN} += "${libdir}/uml"

ALTERNATIVE:${PN} = "tunctl"
ALTERNATIVE_LINK_NAME[tunctl] = "${bindir}/tunctl"
