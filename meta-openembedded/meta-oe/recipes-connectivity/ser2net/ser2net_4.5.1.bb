SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[sha256sum] = "6d60c2eb9e15f6a23743ce7fc3687a8880042d7fca43572e73ca76ed003de258"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/ser2net/files/ser2net"

inherit autotools pkgconfig
