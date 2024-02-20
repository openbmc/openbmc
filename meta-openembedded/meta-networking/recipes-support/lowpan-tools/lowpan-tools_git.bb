SUMMARY = "Utilities for managing the Linux LoWPAN stack"
DESCRIPTION = "This is a set of utils to manage the Linux LoWPAN stack. \
The LoWPAN stack aims for IEEE 802.15.4-2003 (and for lesser extent IEEE 802.15.4-2006) compatibility."
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "flex-native bison-native libnl python"

PV = "0.3.1+git"
SRC_URI = "git://github.com/linux-wpan/lowpan-tools;branch=master;protocol=https \
           file://no-help2man.patch \
           file://0001-Fix-build-errors-with-clang.patch \
           file://0001-addrdb-coord-config-parse.y-add-missing-time.h-inclu.patch \
           file://0001-src-iz.c-Undef-dprintf-before-redefining.patch \
           file://0001-Remove-newline-from-format-line.patch \
           file://0001-coordinator-Fix-strncpy-range-warning.patch \
           file://0001-Fix-potential-string-truncation-in-strncpy.patch \
           "
SRCREV = "1c2d8674cc6f4b1166a066e8822e295c105ae7a2"

S = "${WORKDIR}/git"

inherit autotools python3-dir pkgconfig

CACHED_CONFIGUREVARS += "am_cv_python_pythondir=${PYTHON_SITEPACKAGES_DIR}/lowpan-tools"

CFLAGS += "-Wno-initializer-overrides"

do_install:append() {
    rmdir ${D}${localstatedir}/run
}

FILES:${PN}-dbg += "${libexecdir}/lowpan-tools/.debug/"

PACKAGES =+ "${PN}-python"
FILES:${PN}-python = "${libdir}/python*"

SKIP_RECIPE[lowpan-tools] ?= "WARNING these tools are deprecated! Use wpan-tools instead"
