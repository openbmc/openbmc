require xorg-data-common.inc

SUMMARY = "Common X11 Bitmaps"
LICENSE = "MIT"
DEPENDS += "libxmu"
RDEPENDS_${PN}-dev = ""

LIC_FILES_CHKSUM = "file://COPYING;md5=dbd075aaffa4a60a8d00696f2e4b9a8f"

SRC_URI[md5sum] = "7444bbbd999b53bec6a60608a5301f4c"
SRC_URI[sha256sum] = "3671b034356bbc4d32d052808cf646c940ec8b2d1913adac51b1453e41aa1e9d"
