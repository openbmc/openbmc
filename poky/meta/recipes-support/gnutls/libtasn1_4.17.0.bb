SUMMARY = "Library for ASN.1 and DER manipulation"
DESCRIPTION = "A highly portable C library that encodes and decodes \
DER/BER data following an ASN.1 schema. "
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE:${PN}-bin = "GPLv3+"
LICENSE:${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://doc/COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING;md5=75ac100ec923f959898182307970c360"

SRC_URI = "${GNU_MIRROR}/libtasn1/libtasn1-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           "

DEPENDS = "bison-native"

SRC_URI[md5sum] = "c46f6eb3bd1287031ae5d36465094402"
SRC_URI[sha256sum] = "ece7551cea7922b8e10d7ebc70bc2248d1fdd73351646a2d6a8d68a9421c45a5"

inherit autotools texinfo lib_package gtk-doc

BBCLASSEXTEND = "native nativesdk"
