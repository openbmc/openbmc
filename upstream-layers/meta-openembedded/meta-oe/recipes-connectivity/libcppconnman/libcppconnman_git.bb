SUMMARY = "Amarula libcppconnman library"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a87a7059d580c45495c1218f53e3610d"

SECTION = "libs"

SRC_URI = "git://github.com/amarula/libcppconnman.git;protocol=https;branch=main"
SRCREV = "d89262c2cd7336da8ba9eb06228d422847fe4b20"

inherit cmake pkgconfig

DEPENDS += "glib-2.0"

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DBUILD_CONNMAN=ON"

# LICENSE file
FILES:${PN} += "${datadir}/Amarula"
