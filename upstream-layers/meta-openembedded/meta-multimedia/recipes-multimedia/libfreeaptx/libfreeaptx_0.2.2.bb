SUMMARY = "Free aptX / aptX-HD audio codec library (LGPL fork of openaptx 0.2.0)"
HOMEPAGE = "https://github.com/regularhunter/libfreeaptx"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

PV = "0.2.2"

SRC_URI = "git://github.com/regularhunter/libfreeaptx.git;protocol=https;branch=master"
SRCREV  = "6dee419f934ec781e531f885f7e8e740752e67d1"

inherit pkgconfig

EXTRA_OEMAKE = "\
  LIBDIR=${baselib} \
  CFLAGS='${CFLAGS}' \
  CPPFLAGS='${CPPFLAGS}' \
  LDFLAGS='${LDFLAGS}' \
  CP='cp -a --no-preserve=ownership' \
"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} PREFIX=${prefix} install
}

PACKAGES =+ "${PN}-utils"

FILES:${PN}        += "${libdir}/libfreeaptx.so.*"
FILES:${PN}-dev    += "${includedir}/freeaptx.h ${libdir}/libfreeaptx.so ${libdir}/pkgconfig/libfreeaptx.pc"
FILES:${PN}-utils  += "${bindir}/freeaptxenc ${bindir}/freeaptxdec"
