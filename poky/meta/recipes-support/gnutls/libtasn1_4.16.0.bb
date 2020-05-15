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
           "

DEPENDS = "bison-native"

SRC_URI[md5sum] = "531208de3729d42e2af0a32890f08736"
SRC_URI[sha256sum] = "0e0fb0903839117cb6e3b56e68222771bebf22ad7fc2295a0ed7d576e8d4329d"

inherit autotools texinfo lib_package gtk-doc

BBCLASSEXTEND = "native nativesdk"
