LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gdk-pixbuf popt xcursorgen"

SRC_URI = "http://freedesktop.org/software/${BPN}/releases/${BPN}-${PV}.tar.gz \
           file://0001-Makefile.am-no-examples.patch \
           file://hotspotfix.patch \
           "
SRC_URI[sha256sum] = "05f0216dd0c25a17859de66357f64da5033375b6fbf5f31ca54867311160b64d"

inherit autotools pkgconfig features_check
# because of xcursorgen dependency
REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
