LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gdk-pixbuf popt"

SRC_URI = "http://freedesktop.org/software/${BPN}/releases/${BPN}-${PV}.tar.gz \
           file://0001-Makefile.am-no-examples.patch"
SRC_URI[md5sum] = "5c5374d4f265b0abe4daef1d03f87104"
SRC_URI[sha256sum] = "05f0216dd0c25a17859de66357f64da5033375b6fbf5f31ca54867311160b64d"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
