SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[sha256sum] = "bcb8ae31d00c6b4392d1cd1c9ecc9390a8b241029c42c5eb951af090edaf56db"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/ser2net/files/ser2net"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
