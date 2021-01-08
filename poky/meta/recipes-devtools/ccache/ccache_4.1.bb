SUMMARY = "a fast C/C++ compiler cache"
DESCRIPTION = "ccache is a compiler cache. It speeds up recompilation \
by caching the result of previous compilations and detecting when the \
same compilation is being done again. Supported languages are C, C\+\+, \
Objective-C and Objective-C++."
HOMEPAGE = "http://ccache.samba.org"
SECTION = "devel"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=a66c581f855c1c408730fe5d171e3013"

DEPENDS = "zstd"

SRC_URI = "https://github.com/ccache/ccache/releases/download/v${PV}/${BP}.tar.gz \
           file://0001-Improve-SIMD-detection-735.patch \
           file://0002-Always-use-64bit-to-print-time_t.patch \
           file://0001-blake3-Remove-asm-checks-for-sse-avx.patch \
           "
SRC_URI[sha256sum] = "cdeefb827b3eef3b42b5454858123881a4a90abbd46cc72cf8c20b3bd039deb7"

UPSTREAM_CHECK_URI = "https://github.com/ccache/ccache/releases/"

inherit cmake

PATCHTOOL = "patch"

BBCLASSEXTEND = "native nativesdk"
