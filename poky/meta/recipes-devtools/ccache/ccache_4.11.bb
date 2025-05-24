SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPL-3.0-or-later & MIT & BSL-1.0 & ISC"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=b98c3470e22877fefa43a01a2dd0669c \
                    file://src/third_party/cpp-httplib/httplib.h;endline=6;md5=663aca6f84e7d67ade228aad32afc0ea \
                    file://src/third_party/nonstd-span/nonstd/span.hpp;endline=9;md5=b4af92a7f068b38c5b3410dceb30c186 \
                    file://src/third_party/win32-compat/win32/mktemp.c;endline=17;md5=d287e9c1f1cd2bb2bd164490e1cf449a \
                    "

DEPENDS = "zstd fmt xxhash"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz"

SRC_URI[sha256sum] = "7dba208540dc61cedd5c93df8c960055a35f06e29a0a3cf766962251d4a5c766"

inherit cmake github-releases

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"

# ENABLE_TESTING requires doctest which is not present in oe
EXTRA_OECMAKE += "-DENABLE_TESTING=OFF"
