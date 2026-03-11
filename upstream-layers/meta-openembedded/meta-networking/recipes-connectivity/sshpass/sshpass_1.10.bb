DESCRIPTION = "Non-interactive ssh password auth"
HOMEPAGE = "http://sshpass.sourceforge.net/"
SECTION = "console/network"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "ad1106c203cbb56185ca3bad8c6ccafca3b4064696194da879f81c8d7bdfeeda"

inherit autotools
