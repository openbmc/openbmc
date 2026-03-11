SUMMARY = "A FUSE filesystem for mounting a directory to another location"
DESCRIPTION = "bindfs is a FUSE filesystem for mounting a directory to another location, similarly to mount --bind."
HOMEPAGE = "https://bindfs.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "fuse"
RDEPENDS:${PN} = "fuse"

SRC_URI = "git://github.com/mpartel/bindfs;protocol=https;branch=master;tag=${PV}"
SRCREV = "70a663b3476c0b9b21b626473c00c68a3dc97b92"


inherit autotools pkgconfig gettext
