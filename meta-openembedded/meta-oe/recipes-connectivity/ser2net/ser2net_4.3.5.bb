SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[sha256sum] = "848c4fe863806e506832f1ee85b8b68258f06eb19dad43dbeee16a2cfe5d9053"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/ser2net/files/ser2net"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
