SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=8fc2cae2bbabeb9236cacfa1c83a3dc5"

DEPENDS = "zstd"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://0001-xxhash.h-Fix-build-with-gcc-12.patch \
           "
SRC_URI[sha256sum] = "dc283906b73bd7c461178ca472a459e9d86b5523405035921bd8204e77620264"

inherit cmake github-releases

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"
