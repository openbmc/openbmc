SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=7a19377a02749d8a1281ed608169b0ee"

DEPENDS = "zstd"

SRC_URI = "https://github.com/ccache/ccache/releases/download/v${PV}/${BP}.tar.gz \
           file://0001-xxhash.h-Fix-build-with-gcc-12.patch \
"
SRC_URI[sha256sum] = "59b28a57c3a45e48d6049001999c9f94cd4d3e9b0196994bed9a6a7437ffa3bc"

UPSTREAM_CHECK_URI = "https://github.com/ccache/ccache/releases/"

inherit cmake

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"
