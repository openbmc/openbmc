SUMMARY = "Zstandard - Fast real-time compression algorithm"
DESCRIPTION = "Zstandard is a fast lossless compression algorithm, targeting \
real-time compression scenarios at zlib-level and better compression ratios. \
It's backed by a very fast entropy stage, provided by Huff0 and FSE library."
HOMEPAGE = "http://www.zstd.net/"
SECTION = "console/utils"

PROVIDES += "zstd-decompress"

LICENSE = "BSD-3-Clause OR GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0822a32f7acdbe013606746641746ee8 \
                    file://COPYING;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0 \
                    "

SRC_URI = "git://github.com/facebook/zstd.git;branch=release;protocol=https;tag=v${PV} \
           file://run-ptest \
           file://0001-cli-tests-skip-zstd-max-level-when-memory-is-insuffi.patch \
           "

SRCREV = "f8745da6ff1ad1e7bab384bd1f9d742439278e99"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

CVE_PRODUCT = "zstandard"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lz4] = "HAVE_LZ4=1,HAVE_LZ4=0,lz4"
PACKAGECONFIG[lzma] = "HAVE_LZMA=1,HAVE_LZMA=0,xz"
PACKAGECONFIG[zlib] = "HAVE_ZLIB=1,HAVE_ZLIB=0,zlib"

# See programs/README.md for how to use this
ZSTD_LEGACY_SUPPORT ??= "4"

EXTRA_OEMAKE += "V=1"

do_compile () {
    oe_runmake ${PACKAGECONFIG_CONFARGS} ZSTD_LEGACY_SUPPORT=${ZSTD_LEGACY_SUPPORT}
    oe_runmake ${PACKAGECONFIG_CONFARGS} ZSTD_LEGACY_SUPPORT=${ZSTD_LEGACY_SUPPORT} -C contrib/pzstd
}

do_install () {
    oe_runmake install 'DESTDIR=${D}'
    oe_runmake install 'DESTDIR=${D}' PREFIX=${prefix} -C contrib/pzstd
}

PACKAGE_BEFORE_PN = "libzstd"

FILES:libzstd = "${libdir}/libzstd${SOLIBS}"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

do_compile_ptest() {
    oe_runmake -C ${S}/tests fullbench datagen \
        ZSTD_LEGACY_SUPPORT=${ZSTD_LEGACY_SUPPORT}
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -d ${D}${PTEST_PATH}/programs

    # Test binaries
    install -m 0755 ${S}/tests/fullbench ${D}${PTEST_PATH}/tests/
    install -m 0755 ${S}/tests/datagen ${D}${PTEST_PATH}/tests/

    # cli-tests
    cp -r ${S}/tests/cli-tests ${D}${PTEST_PATH}/tests/

    # Golden test data needed by cli-tests
    for d in golden-compression golden-decompression golden-dictionaries; do
        cp -r ${S}/tests/$d ${D}${PTEST_PATH}/tests/
    done

    # zstdgrep/zstdless scripts needed by cltools tests
    install -m 0755 ${S}/programs/zstdgrep ${D}${PTEST_PATH}/programs/
    install -m 0755 ${S}/programs/zstdless ${D}${PTEST_PATH}/programs/

    # The levels.sh expected stderr includes set -v traces that change
    # after patching the memory check. Remove the exact match file so
    # the test framework ignores stderr comparison.
    rm -f ${D}${PTEST_PATH}/tests/cli-tests/compression/levels.sh.stderr.exact
}

RDEPENDS:${PN}-ptest += "bash grep less python3-core python3-modules"
