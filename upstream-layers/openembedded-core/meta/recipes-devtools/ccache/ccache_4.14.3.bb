SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "BSL-1.0 AND GPL-3.0-or-later AND ISC AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=e4a0db390c3eeb742b398ac010a70913 \
                    file://src/third_party/cpp-httplib/httplib.h;endline=6;md5=663aca6f84e7d67ade228aad32afc0ea \
                    file://src/third_party/win32-compat/win32/mktemp.c;endline=17;md5=d287e9c1f1cd2bb2bd164490e1cf449a \
                    "

DEPENDS = "zstd fmt xxhash"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz"

SRC_URI[sha256sum] = "d50587abd5b787a7ba48cb1d76f7a4f09036e0181e742fc7383a8b154d73f0ba"

inherit cmake github-releases

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG[docs] = "-DENABLE_DOCUMENTATION=ON,-DENABLE_DOCUMENTATION=OFF,asciidoc"
PACKAGECONFIG[redis] = "-DREDIS_STORAGE_BACKEND=ON,-DREDIS_STORAGE_BACKEND=OFF,hiredis"

# ENABLE_TESTING requires doctest which is not present in oe
EXTRA_OECMAKE += "-DENABLE_TESTING=OFF"
