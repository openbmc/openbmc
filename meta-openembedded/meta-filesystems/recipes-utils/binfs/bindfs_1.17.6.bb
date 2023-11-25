SUMMARY = "A FUSE filesystem for mounting a directory to another location"
DESCRIPTION = "bindfs is a FUSE filesystem for mounting a directory to another location, similarly to mount --bind."
HOMEPAGE = "https://bindfs.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "fuse"
RDEPENDS:${PN} = "fuse"

SRC_URI = "git://github.com/mpartel/bindfs;protocol=https;branch=master"
SRCREV = "b982aee1161307cdd0e90f7f9597b11872440efc"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext
