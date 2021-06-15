DESCRIPTION = "sigrok-cli is a command-line frontend for sigrok."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsigrok"

PACKAGECONFIG[decode] = "--with-libsigrokdecode,--without-libsigrokdecode,libsigrokdecode"

PACKAGECONFIG ??= "decode"

inherit autotools pkgconfig mime-xdg

SRC_URI = "http://sigrok.org/download/source/sigrok-cli/sigrok-cli-${PV}.tar.gz"
SRC_URI[md5sum] = "856fd496cd99d1091aa128405c522a36"
SRC_URI[sha256sum] = "71d0443f36897bf565732dec206830dbea0f2789b6601cf10536b286d1140ab8"

FILES_${PN} += "${datadir}/icons/hicolor"
