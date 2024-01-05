require xorg-data-common.inc

SUMMARY = "Default set of cursor themes for use with libXcursor"

DEPENDS:append = " libxcursor xcursorgen-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=d8cbd99fff773f92e844948f74ef0df8"

SRC_URI[sha256sum] = "95bae8f48823d894a05bf42dfbf453674ab7dbdeb11e2bc079e8525ad47378c8"

FILES:${PN} += "${datadir}/icons"
