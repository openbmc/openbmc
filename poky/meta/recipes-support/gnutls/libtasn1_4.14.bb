SUMMARY = "Library for ASN.1 and DER manipulation"
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://doc/COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE;md5=75ac100ec923f959898182307970c360"

SRC_URI = "${GNU_MIRROR}/libtasn1/libtasn1-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           file://fix-ldflags.patch \
           file://fix-gtkdoc.patch \
           "

DEPENDS = "bison-native"

SRC_URI[md5sum] = "e9918200ed4a778e2b3cbe34c1be4205"
SRC_URI[sha256sum] = "9e604ba5c5c8ea403487695c2e407405820d98540d9de884d6e844f9a9c5ba08"

inherit autotools texinfo lib_package gtk-doc

BBCLASSEXTEND = "native nativesdk"
