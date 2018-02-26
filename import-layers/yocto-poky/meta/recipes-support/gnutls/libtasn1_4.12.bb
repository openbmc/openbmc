SUMMARY = "Library for ASN.1 and DER manipulation"
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    file://README;endline=8;md5=c3803a3e8ca5ab5eb1e5912faa405351"

SRC_URI = "${GNU_MIRROR}/libtasn1/libtasn1-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           file://0001-stdint.m4-reintroduce-GNULIB_OVERRIDES_WINT_T-check.patch \
           file://CVE-2017-10790.patch \
           "

DEPENDS = "bison-native"

SRC_URI[md5sum] = "5c724bd1f73aaf4a311833e1cd297b21"
SRC_URI[sha256sum] = "6753da2e621257f33f5b051cc114d417e5206a0818fe0b1ecfd6153f70934753"

inherit autotools texinfo binconfig lib_package gtk-doc

BBCLASSEXTEND = "native"
