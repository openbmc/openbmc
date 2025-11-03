SUMMARY = "Extremely Fast Compression algorithm"
DESCRIPTION = "LZ4 is a very fast lossless compression algorithm, providing compression speed at 400 MB/s per core, scalable with multi-cores CPU. It also features an extremely fast decoder, with speed in multiple GB/s per core, typically reaching RAM speed limits on multi-core systems."
HOMEPAGE = "https://github.com/lz4/lz4"

LICENSE = "BSD-2-Clause | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://lib/LICENSE;md5=5cd5f851b52ec832b10eedb3f01f885a \
                    file://programs/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE;md5=c5cc3cd6f9274b4d32988096df9c3ec3 \
                    "

PE = "1"

SRCREV = "5ff839680134437dbf4678f3d0c7b371d84f4964"

SRC_URI = "git://github.com/lz4/lz4.git;branch=release;protocol=https \
           file://run-ptest \
           file://CVE-2025-62813.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit ptest

CVE_STATUS[CVE-2014-4715] = "fixed-version: Fixed in r118, which is larger than the current version."

EXTRA_OEMAKE = "PREFIX=${prefix} CC='${CC}' CFLAGS='${CFLAGS}' DESTDIR=${D} LIBDIR=${libdir} INCLUDEDIR=${includedir} BUILD_STATIC=no"

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

