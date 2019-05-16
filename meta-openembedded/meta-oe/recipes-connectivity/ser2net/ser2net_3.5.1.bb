SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[md5sum] = "569267b37b8f507d8874f28f5334b5d2"
SRC_URI[sha256sum] = "02f5dd0abbef5a17b80836b0de1ef0588e257106fb5e269b86822bfd001dc862"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
