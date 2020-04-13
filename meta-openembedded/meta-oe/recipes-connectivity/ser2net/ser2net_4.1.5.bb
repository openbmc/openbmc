SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[md5sum] = "52c5e56d2d54ced0cdeb764a7e8fec92"
SRC_URI[sha256sum] = "df904d271eb161c265c956f0cb938dd0a375dda4a919a344f73b08bc50b9f308"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
