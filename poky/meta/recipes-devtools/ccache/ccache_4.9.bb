SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=11b0b581dd56bbac4f5de214d47927ca"

DEPENDS = "zstd"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://0001-xxhash.h-Fix-build-with-gcc-12.patch \
           "
SRC_URI[sha256sum] = "866b2223d59333640f0e7a003cbb85b32d9ca3c9445bd9e3cf142942e69dd3ec"

inherit cmake github-releases

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"
