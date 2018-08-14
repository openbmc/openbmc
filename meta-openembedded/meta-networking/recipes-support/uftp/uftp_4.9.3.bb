DESCRIPTION = "Encrypted UDP based FTP with multicast"
HOMEPAGE = "https://sourceforge.net/projects/uftp-multicast"
SECTION = "libs/network"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${SOURCEFORGE_MIRROR}/uftp-multicast/source-tar/uftp-${PV}.tar.gz"
SRC_URI[md5sum] = "231d6ba7820d89a712fe7d727ab0f8e6"
SRC_URI[sha256sum] = "9e9215af0315257c6cc4f40fbc6161057e861be1fff10a38a5564f699e99c78f"

DEPENDS = "openssl"

do_install () {
	oe_runmake install DESTDIR=${D}
}
