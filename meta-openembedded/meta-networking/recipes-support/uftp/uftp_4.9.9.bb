DESCRIPTION = "Encrypted UDP based FTP with multicast"
HOMEPAGE = "https://sourceforge.net/projects/uftp-multicast"
SECTION = "libs/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/uftp-multicast/files/source-tar/"

SRC_URI = "${SOURCEFORGE_MIRROR}/uftp-multicast/source-tar/uftp-${PV}.tar.gz"
SRC_URI[md5sum] = "154e2c82a33fd4999040f8836e2dca2c"
SRC_URI[sha256sum] = "c04bc75a88fc3d57504269f260be4d0b1bc440508b5a5ca587df6c16b771aa48"

DEPENDS = "openssl"

do_install () {
	oe_runmake install DESTDIR=${D}
}
