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
           file://0001-configure-don-t-add-Werror-to-build-flags.patch \
           file://0002-ASN.y-corrected-compiler-warning.patch \
           file://0003-parser_aux-corrected-potential-null-pointer-derefere.patch \
           file://0004-tools-eliminated-compiler-warnings.patch \
           "

SRC_URI[md5sum] = "3018d0f466a32b66dde41bb122e6cab6"
SRC_URI[sha256sum] = "4f6f7a8fd691ac2b8307c8ca365bad711db607d4ad5966f6938a9d2ecd65c920"

inherit autotools texinfo binconfig lib_package gtk-doc

BBCLASSEXTEND = "native"
