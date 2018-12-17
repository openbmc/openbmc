DESCRIPTION = "Encrypted UDP based FTP with multicast"
HOMEPAGE = "https://sourceforge.net/projects/uftp-multicast"
SECTION = "libs/network"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${SOURCEFORGE_MIRROR}/uftp-multicast/source-tar/uftp-${PV}.tar.gz"
SRC_URI[md5sum] = "f7a5affd3000b5aafbb13df49719b6c0"
SRC_URI[sha256sum] = "e98c6318e497124d777ca71eae752d213207c35de9f782c8bcaaf82ece20e599"

DEPENDS = "openssl"

do_install () {
	oe_runmake install DESTDIR=${D}
}
