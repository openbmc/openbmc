SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9911525d4128bee234ee2d3ccaa2537"

SRC_URI = "https://github.com/akheron/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
           file://0001-Honour-multilib-paths.patch \
           file://0001-Only-export-symbols-starting-with-json_-and-jansson_.patch \
           "
SRC_URI[sha256sum] = "a7eac7765000373165f9373eb748be039c10b2efc00be9af3467ec92357d8954"

UPSTREAM_CHECK_URI = "https://github.com/akheron/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DJANSSON_BUILD_SHARED_LIBS=${@ 'OFF' if d.getVar('DISABLE_STATIC') == '' else 'ON' }"

BBCLASSEXTEND = "native"
