DESCRIPTION = "USB Device Firmware Upgrade utility"
AUTHOR = "Harald Welte <laforge@openmoko.org>"
HOMEPAGE = "http://dfu-util.sourceforge.net"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libusb1"

SRC_URI = "http://dfu-util.sourceforge.net/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "233bb1e08ef4b405062445d84e28fde6"
SRC_URI[sha256sum] = "36428c6a6cb3088cad5a3592933385253da5f29f2effa61518ee5991ea38f833"

inherit autotools pkgconfig
