SUMMARY = "Extremely Fast Compression algorithm"
DESCRIPTION = "LZ4 is a very fast lossless compression algorithm, providing compression speed at 400 MB/s per core, scalable with multi-cores CPU. It also features an extremely fast decoder, with speed in multiple GB/s per core, typically reaching RAM speed limits on multi-core systems."
HOMEPAGE = "https://github.com/lz4/lz4"

LICENSE = "BSD-2-Clause | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://lib/LICENSE;md5=5cd5f851b52ec832b10eedb3f01f885a \
                    file://programs/COPYING;md5=492daf447d6db0e5eb344a7922e7ec25 \
                    file://LICENSE;md5=c111c47e301c2ffe8776729b40b44477 \
                    "

PE = "1"

SRCREV = "ebb370ca83af193212df4dcbadcc5d87bc0de2f0"

SRC_URI = "git://github.com/lz4/lz4.git;branch=release;protocol=https \
           file://reproducibility.patch \
           file://run-ptest"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit ptest

CVE_STATUS[CVE-2014-4715] = "fixed-version: Fixed in r118, which is larger than the current version."

EXTRA_OEMAKE = "DESTDIR=${D} BUILD_STATIC=no"

do_install() {
	oe_runmake install
}

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-ptest += "bash"

do_compile_ptest() {
        oe_runmake -C ${B}/tests/
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests/
	install --mode=755 ${B}/tests/frametest ${D}${PTEST_PATH}/tests/
	sed -i "s#@PTEST_PATH@#${PTEST_PATH}#g" ${D}${PTEST_PATH}/run-ptest

}

