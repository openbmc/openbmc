SUMMARY = "libusb-0.1 compatibility layer for libusb1"
DESCRIPTION = "libusb-0.1 compatible layer for libusb1, a drop-in replacement \
that aims to look, feel and behave exactly like libusb-0.1"
HOMEPAGE = "http://www.libusb.org/"
BUGTRACKER = "http://www.libusb.org/report"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f2ac5f3ac4835e8f91324a26a590a423"
DEPENDS = "libusb1"

# libusb-compat dlopen() libusb1 so we need to explicitly RDEPENDS on it
RDEPENDS:${PN} += "libusb1"

# Few packages are known not to work with libusb-compat (e.g. libmtp-1.0.0),
# so here libusb-0.1 is removed completely instead of adding virtual/libusb0.
# Besides, libusb-0.1 uses a per 1ms polling that hurts a lot to power
# consumption.
PROVIDES = "libusb virtual/libusb0"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

SRC_URI = " \
    git://github.com/libusb/libusb-compat-0.1.git;protocol=https;branch=master \
    file://0001-usb.h-Include-sys-types.h.patch \
    file://0002-automake-make-example-programs-installable.patch \
    file://run-ptest \
"
SRCREV = "c497eff1ae8c4cfd4fdff370f04c78fa0584f4f3"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/libusb/libusb-compat-0.1/releases"

BINCONFIG = "${bindir}/libusb-config"

inherit autotools pkgconfig binconfig-disabled lib_package ptest

# examples are used as ptest so enable them at configuration if needed
EXTRA_OECONF += "${@bb.utils.contains('PTEST_ENABLED', '1', '--enable-examples-build', '', d)}"

# Move test binaries out of bindir to avoid clashing with a "real" lsusb.
do_install_ptest() {
    for bin in lsusb testlibusb; do
       mv ${D}${bindir}/$bin ${D}${PTEST_PATH}
    done
}
