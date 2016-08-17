SUMMARY = "This is a filesystem client based on the SSH File Transfer Protocol using FUSE"
AUTHOR = "Miklos Szeredi <miklos@szeredi.hu>"
HOMEPAGE = "http://fuse.sourceforge.net/sshfs.html"
SECTION = "console/network"
LICENSE = "GPLv2"
DEPENDS = "glib-2.0 fuse"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SOURCEFORGE_MIRROR}/fuse/${BP}.tar.gz"
S = "${WORKDIR}/${BP}"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/sshnodelay.so"

SRC_URI[md5sum] = "17494910db8383a366b1301e5f5148a9"
SRC_URI[sha256sum] = "e9171452e5d0150b9c6a2158fd2e2dcefb5d5d03ba4d208949e00a3a46c6e63e"
