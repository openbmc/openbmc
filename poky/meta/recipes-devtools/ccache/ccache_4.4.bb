SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=73963d63171ecbdf2d25274de67c68c5"

DEPENDS = "zstd"

SRC_URI = "https://github.com/ccache/ccache/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "61a993d62216aff35722a8d0e8ffef9b677fc3f6accd8944ffc2a6db98fb3142"

UPSTREAM_CHECK_URI = "https://github.com/ccache/ccache/releases/"

inherit cmake

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"
