SUMMARY = "A Python module for working with OpenPGP messages"
HOMEPAGE = "https://launchpad.net/pygpgme"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://README;md5=2dc15a76acf01e126188c8de634ae4b3"

SRC_URI = "https://launchpad.net/pygpgme/trunk/${PV}/+download/pygpgme-${PV}.tar.gz"
SRC_URI[md5sum] = "d38355af73f0352cde3d410b25f34fd0"
SRC_URI[sha256sum] = "5fd887c407015296a8fd3f4b867fe0fcca3179de97ccde90449853a3dfb802e1"

S = "${WORKDIR}/pygpgme-${PV}"

inherit distutils3

DEPENDS = "gpgme python3"

RDEPENDS_${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
