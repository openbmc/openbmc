SUMMARY = "Library for ASN.1 and DER manipulation"
DESCRIPTION = "A highly portable C library that encodes and decodes \
DER/BER data following an ASN.1 schema. "
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

LICENSE = "GPL-3.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN}-bin = "GPL-3.0-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://doc/COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING;md5=75ac100ec923f959898182307970c360"

SRC_URI = "${GNU_MIRROR}/libtasn1/libtasn1-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           "

DEPENDS = "bison-native"

SRC_URI[sha256sum] = "1613f0ac1cf484d6ec0ce3b8c06d56263cc7242f1c23b30d82d23de345a63f7a"

inherit autotools texinfo lib_package gtk-doc

BBCLASSEXTEND = "native nativesdk"
