SUMMARY = "Replacement for the old crypt() package and crypt(1) command, with extensions"
HOMEPAGE = "http://mcrypt.sourceforge.net/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff"
DEPENDS = "libtool"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/mcrypt/Libmcrypt/${PV}/libmcrypt-${PV}.tar.gz"

SRC_URI[md5sum] = "0821830d930a86a5c69110837c55b7da"
SRC_URI[sha256sum] = "e4eb6c074bbab168ac47b947c195ff8cef9d51a211cdd18ca9c9ef34d27a373e"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/mcrypt/files/Libmcrypt/"
UPSTREAM_CHECK_REGEX = "Libmcrypt/(?P<pver>\d+(\.\d+)+)/"

inherit autotools-brokensep gettext binconfig
