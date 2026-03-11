SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9911525d4128bee234ee2d3ccaa2537"

SRC_URI = "https://github.com/akheron/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
           file://0001-Honour-multilib-paths.patch \
           file://0001-Only-export-symbols-starting-with-json_-and-jansson_.patch \
           file://0002-allow-build-with-cmake-4.patch \
           "
SRC_URI[sha256sum] = "6bd82d3043dadbcd58daaf903d974891128d22aab7dada5d399cb39094af49ce"

UPSTREAM_CHECK_URI = "https://github.com/akheron/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DJANSSON_BUILD_SHARED_LIBS=${@ 'OFF' if d.getVar('DISABLE_STATIC') == '' else 'ON' }"

BBCLASSEXTEND = "native"
