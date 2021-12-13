SUMMARY = "Zstandard - Fast real-time compression algorithm"
DESCRIPTION = "Zstandard is a fast lossless compression algorithm, targeting \
real-time compression scenarios at zlib-level and better compression ratios. \
It's backed by a very fast entropy stage, provided by Huff0 and FSE library."
HOMEPAGE = "http://www.zstd.net/"
SECTION = "console/utils"

LICENSE = "BSD-3-Clause & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7f0b161edbe52f5f345a3d1311d0b32 \
                    file://COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

SRC_URI = "git://github.com/facebook/zstd.git;branch=release;protocol=https \
           file://0001-Makefile-sort-all-wildcard-file-list-expansions.patch \
           file://0001-MinGW-Build-Fixes.patch \
           "

SRCREV = "a488ba114ec17ea1054b9057c26a046fc122b3b6"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

CVE_PRODUCT = "zstandard"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lz4] = "HAVE_LZ4=1,HAVE_LZ4=0,lz4"
PACKAGECONFIG[lzma] = "HAVE_LZMA=1,HAVE_LZMA=0,xz"
PACKAGECONFIG[zlib] = "HAVE_ZLIB=1,HAVE_ZLIB=0,zlib"

# See programs/README.md for how to use this
ZSTD_LEGACY_SUPPORT ??= "4"

do_compile () {
    oe_runmake ${PACKAGECONFIG_CONFARGS} ZSTD_LEGACY_SUPPORT=${ZSTD_LEGACY_SUPPORT}
    oe_runmake ${PACKAGECONFIG_CONFARGS} ZSTD_LEGACY_SUPPORT=${ZSTD_LEGACY_SUPPORT} -C contrib/pzstd
}

do_install () {
    oe_runmake install 'DESTDIR=${D}'
    oe_runmake install 'DESTDIR=${D}' PREFIX=${prefix} -C contrib/pzstd
}

BBCLASSEXTEND = "native nativesdk"
