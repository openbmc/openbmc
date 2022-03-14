SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=8fe0fdfdc7a892516526b86c28d06a16"

DEPENDS = "zstd"

SRC_URI = "https://github.com/ccache/ccache/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "f0d3cff5d555d6868f14a7d05696f0370074e475304fd5aa152b98f892364981"

UPSTREAM_CHECK_URI = "https://github.com/ccache/ccache/releases/"

inherit cmake

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"
