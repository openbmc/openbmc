SUMMARY = "GNU Chess is a chess-playing program."
HOMEPAGE = "http://www.gnu.org/software/chess/"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/chess/${BP}.tar.gz"
SRC_URI[sha256sum] = "0b37bec2098c2ad695b7443e5d7944dc6dc8284f8d01fcc30bdb94dd033ca23a"

inherit autotools gettext

do_configure:prepend() {
    touch ${S}/ABOUT-NLS
    touch ${S}/man/gnuchess.1
}

FILES:${PN} += "${datadir}"
