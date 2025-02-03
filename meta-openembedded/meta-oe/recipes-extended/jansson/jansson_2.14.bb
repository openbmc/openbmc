SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=afd92c4cfc08f4896003251b878cc0bf"

SRC_URI = "https://github.com/akheron/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
           file://0001-Fix-overwriting-linker-flags.patch \
           file://0001-Honour-multilib-paths.patch \
           file://0001-add-back-JSON_INTEGER_IS_LONG_LONG-for-cmake.patch \
           file://0001-Only-export-symbols-starting-with-json_-and-jansson_.patch \
           "
SRC_URI[sha256sum] = "fba956f27c6ae56ce6dfd52fbf9d20254aad42821f74fa52f83957625294afb9"

UPSTREAM_CHECK_URI = "https://github.com/akheron/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DJANSSON_BUILD_SHARED_LIBS=${@ 'OFF' if d.getVar('DISABLE_STATIC') == '' else 'ON' }"

BBCLASSEXTEND = "native"
