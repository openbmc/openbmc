SUMMARY = "Library handling the communication with Apple's Tatsu Signing Server (TSS)"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

SRC_URI = "https://github.com/libimobiledevice/libtatsu/releases/download/${PV}/libtatsu-${PV}.tar.bz2"
UPSTREAM_CHECK_URI = "https://github.com/libimobiledevice/libtatsu/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+(\.\d+)+)"
SRC_URI[sha256sum] = "96c8abd97552a4a0c487c701aba9a65d590c338dd331058ffb38304290b696c7"

DEPENDS = "curl libplist"

inherit pkgconfig autotools
