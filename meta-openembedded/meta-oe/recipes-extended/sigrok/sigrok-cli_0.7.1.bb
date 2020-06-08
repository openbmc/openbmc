DESCRIPTION = "sigrok-cli is a command-line frontend for sigrok."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsigrok"

PACKAGECONFIG[decode] = "--with-libsigrokdecode,--without-libsigrokdecode,libsigrokdecode"

PACKAGECONFIG ??= "decode"

inherit autotools pkgconfig mime-xdg

SRC_URI = "http://sigrok.org/download/source/sigrok-cli/sigrok-cli-${PV}.tar.gz"
SRC_URI[md5sum] = "3f45ce664bad529d8b3f78a61b017d75"
SRC_URI[sha256sum] = "f52413429f47d457c333db0fd068416ab7a3f9e35ca76de8624dc5ac6fb07797"

FILES_${PN} += "${datadir}/icons/hicolor"
