SUMMARY = "This is a filesystem client based on the SSH File Transfer Protocol using FUSE"
AUTHOR = "Miklos Szeredi <miklos@szeredi.hu>"
HOMEPAGE = "http://fuse.sourceforge.net/sshfs.html"
SECTION = "console/network"
LICENSE = "GPLv2"
DEPENDS = "glib-2.0 fuse"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/libfuse/sshfs;branch=sshfs_2.x \
"
SRCREV = "42dfaf9a5bdc1ee429c7718e8788ca37a5763e0d"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/sshnodelay.so"
