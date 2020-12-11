SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[sha256sum] = "5e407d684d0aa0919ddd15af368f890c5940cddd6034b7efc363823f38f6ff0c"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/ser2net/files/ser2net"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
