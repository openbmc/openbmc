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
           "

DEPENDS = "bison-native"

SRC_URI[md5sum] = "ce2ba4d3088119b48e7531a703669c52"
SRC_URI[sha256sum] = "7e528e8c317ddd156230c4e31d082cd13e7ddeb7a54824be82632209550c8cca"

inherit autotools texinfo lib_package gtk-doc

BBCLASSEXTEND = "native"
