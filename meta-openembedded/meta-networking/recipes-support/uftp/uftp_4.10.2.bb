DESCRIPTION = "Encrypted UDP based FTP with multicast"
HOMEPAGE = "https://sourceforge.net/projects/uftp-multicast"
SECTION = "libs/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/uftp-multicast/files/source-tar/"

SRC_URI = "${SOURCEFORGE_MIRROR}/uftp-multicast/source-tar/uftp-${PV}.tar.gz"
SRC_URI[md5sum] = "db0519bf7b1e0329449e81c1e68262f8"
SRC_URI[sha256sum] = "ecab6ab07fe0ebaf7bfe35d99fe2da28ede3ddc6f21f825d3b259cf171258505"

DEPENDS = "openssl"

do_install () {
	oe_runmake install DESTDIR=${D}
}
