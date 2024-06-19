DESCRIPTION = "USB Device Firmware Upgrade utility"
HOMEPAGE = "http://dfu-util.sourceforge.net"
SECTION = "devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libusb1"

SRC_URI = "http://dfu-util.sourceforge.net/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "b4b53ba21a82ef7e3d4c47df2952adf5fa494f499b6b0b57c58c5d04ae8ff19e"

inherit autotools pkgconfig

BBCLASSEXTEND = "nativesdk"
