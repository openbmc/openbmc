SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[md5sum] = "562274d783534276a9feac913b7d8c4e"
SRC_URI[sha256sum] = "d846066e27c3072565990745d030357aa0c278f96b7d1d4f59023347c1db8824"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
