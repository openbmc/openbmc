LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libxres libxext virtual/libx11 ncurses"

SRC_URI = "http://downloads.yoctoproject.org/releases/xrestop/xrestop-0.4.tar.gz \
           file://readme.patch.gz \
           "
UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/xrestop-0.4"

SRC_URI[md5sum] = "d8a54596cbaf037e62b80c4585a3ca9b"
SRC_URI[sha256sum] = "67c2fc94a7ecedbaae0d1837e82e93d1d98f4a6d759828860e552119af3ce257"

inherit autotools pkgconfig

EXCLUDE_FROM_WORLD = "1"
