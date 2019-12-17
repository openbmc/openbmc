DESCRIPTION = "Encrypted UDP based FTP with multicast"
HOMEPAGE = "https://sourceforge.net/projects/uftp-multicast"
SECTION = "libs/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/uftp-multicast/files/source-tar/"

SRC_URI = "${SOURCEFORGE_MIRROR}/uftp-multicast/source-tar/uftp-${PV}.tar.gz"
SRC_URI[md5sum] = "df6407af3a0067b881431b3dad149ef3"
SRC_URI[sha256sum] = "91ba8aae80c7c9ccaf04600b628cbeca4699ed48268fe43d2bf539a41083f292"

DEPENDS = "openssl"

do_install () {
	oe_runmake install DESTDIR=${D}
}
