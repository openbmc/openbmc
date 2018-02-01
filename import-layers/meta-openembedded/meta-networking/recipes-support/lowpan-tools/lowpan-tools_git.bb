SUMMARY = "Utilities for managing the Linux LoWPAN stack"
DESCRIPTION = "This is a set of utils to manage the Linux LoWPAN stack. \
The LoWPAN stack aims for IEEE 802.15.4-2003 (and for lesser extent IEEE 802.15.4-2006) compatibility."
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "flex-native bison-native libnl python"

PV = "0.3.1+git${SRCPV}"
SRC_URI = "git://git.code.sf.net/p/linux-zigbee/linux-zigbee \
           file://no-help2man.patch \
           file://0001-Fix-build-errors-with-clang.patch \
           file://0001-addrdb-coord-config-parse.y-add-missing-time.h-inclu.patch \
           file://0001-src-iz.c-Undef-dprintf-before-redefining.patch \
           file://0001-Remove-newline-from-format-line.patch \
           "
SRCREV = "38f42dbfce9e13629263db3bd3b81f14c69bb733"

S = "${WORKDIR}/git"

inherit autotools python-dir pkgconfig

CACHED_CONFIGUREVARS += "am_cv_python_pythondir=${PYTHON_SITEPACKAGES_DIR}/lowpan-tools"

CFLAGS += "-Wno-initializer-overrides"

do_install_append() {
    rmdir ${D}${localstatedir}/run
}

FILES_${PN}-dbg += "${libexecdir}/lowpan-tools/.debug/"

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${libdir}/python*"
