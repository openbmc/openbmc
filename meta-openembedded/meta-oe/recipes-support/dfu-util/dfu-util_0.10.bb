DESCRIPTION = "USB Device Firmware Upgrade utility"
AUTHOR = "Harald Welte <laforge@openmoko.org>"
HOMEPAGE = "http://dfu-util.sourceforge.net"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libusb1"

SRC_URI = "http://dfu-util.sourceforge.net/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "8cf55663703cdc6b40f377f999eb8d3d"
SRC_URI[sha256sum] = "a03dc58dfc79c056819c0544b2a5970537566460102b3d82cfb038c60e619b42"

inherit autotools pkgconfig
