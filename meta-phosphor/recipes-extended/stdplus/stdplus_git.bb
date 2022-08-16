SUMMARY = "Common C++ functions"
DESCRIPTION = "Common C++ functions."
HOMEPAGE = "http://github.com/openbmc/stdplus"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += " \
  fmt \
  liburing \
  "

PACKAGES =+ "libstdplus libstdplus-io_uring"
FILES:libstdplus = "${libdir}/libstdplus.so.*"
FILES:libstdplus-io_uring = "${libdir}/libstdplus-io_uring.so.*"

EXTRA_OEMESON = " \
        -Dexamples=false \
        -Dtests=disabled \
        -Dgtest=disabled \
        "

SRC_URI += "git://github.com/openbmc/stdplus;branch=master;protocol=https"
SRCREV = "8e30a515d13c64d95bf1085c01070089bac5b43f"

S = "${WORKDIR}/git"
