SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "bf19d4cddce260b3c3e1d51d42509071"
SRC_URI[sha256sum] = "e8f2efd235f842bad2f6938bf4a72240a5e5fcd248e8444335e63beb60fabd82"

inherit autotools allarch

PACKAGES = "${PN} ${PN}-doc"

FILES_${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
