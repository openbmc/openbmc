SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "http://tukaani.org/xz/"
SECTION = "base"

# The source includes bits of PD, GPLv2, GPLv3, LGPLv2.1+, but the only file
# which is GPLv3 is an m4 macro which isn't shipped in any of our packages,
# and the LGPL bits are under lib/, which appears to be used for libgnu, which
# appears to be used for DOS builds. So we're left with GPLv2+ and PD.
LICENSE = "GPLv2+ & GPL-3.0-with-autoconf-exception & LGPLv2.1+ & PD"
LICENSE_${PN} = "GPLv2+"
LICENSE_${PN}-dev = "GPLv2+"
LICENSE_${PN}-staticdev = "GPLv2+"
LICENSE_${PN}-doc = "GPLv2+"
LICENSE_${PN}-dbg = "GPLv2+"
LICENSE_${PN}-locale = "GPLv2+"
LICENSE_liblzma = "PD"

LIC_FILES_CHKSUM = "file://COPYING;md5=97d554a32881fee0aa283d96e47cb24a \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://lib/getopt.c;endline=23;md5=2069b0ee710572c03bb3114e4532cd84 \
                    "

SRC_URI = "http://tukaani.org/xz/xz-${PV}.tar.gz"
SRC_URI[md5sum] = "5ace3264bdd00c65eeec2891346f65e6"
SRC_URI[sha256sum] = "b512f3b726d3b37b6dc4c8570e137b9311e7552e8ccbab4d39d47ce5f4177145"
UPSTREAM_CHECK_REGEX = "xz-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext

PACKAGES =+ "liblzma"

FILES_liblzma = "${libdir}/liblzma*${SOLIBS}"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "xz xzcat unxz \
                     lzma lzcat unlzma"

BBCLASSEXTEND = "native nativesdk"

export CONFIG_SHELL="/bin/sh"
