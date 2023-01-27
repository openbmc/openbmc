SUMMARY = "GNU Chess is a chess-playing program."
HOMEPAGE = "http://www.gnu.org/software/chess/"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://git.savannah.gnu.org/cgit/chess.git/snapshot/chess-${PV}.tar.gz \
           file://0001-Remove-register-storage-class-classifier.patch"
SRC_URI[sha256sum] = "03f9e844ccdd48d20ee49314174404f8b643d83bb8ce9ec9d2e6a21f1b6fb9f5"

S = "${WORKDIR}/chess-${PV}"

inherit autotools gettext

do_configure:prepend() {
    touch ${S}/ABOUT-NLS
    touch ${S}/man/gnuchess.1
}

FILES:${PN} += "${datadir}"
