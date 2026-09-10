DESCRIPTION = "A regular expression library"
HOMEPAGE = "https://github.com/google/re2/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b5c31eb512bdf3cb11ffd5713963760"

# tag 2025-11-05
SRCREV = "927f5d53caf8111721e734cf24724686bb745f55"

SRC_URI = "git://github.com/google/re2.git;branch=main;protocol=https;tag=2025-11-05 \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)} \
          "

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(-\d+)+)"

# The tag regex above does find the newest upstream tag (2025-11-05), but
# upstream tags use "-" separators while PV uses ".", and bb.utils.vercmp_string
# parses "2025-11-05" as version "2025-11" + revision "05", so the two can never
# compare equal. Flag the version as unknown rather than permanently broken: a
# genuinely newer upstream tag still shows up as KNOWN_BROKEN.
UPSTREAM_VERSION_UNKNOWN = "1"

DEPENDS = "abseil-cpp ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest googlebenchmark', '', d)}"

inherit cmake ptest
RDEPENDS:${PN}-ptest += "cmake sed"

EXTRA_OECMAKE += " \
	-DBUILD_SHARED_LIBS=ON \
	${@bb.utils.contains('PTEST_ENABLED', '1', '-DRE2_BUILD_TESTING=ON', '-DRE2_BUILD_TESTING=OFF', d)} \
"
# | riscv32-yoe-linux-ld.lld: error: undefined reference: __atomic_load_8
# | >>> referenced by libtesting.so (disallowed by --no-allow-shlib-undefined)
LDFLAGS:append:riscv32 = " -latomic"

do_install_ptest () {
    cp -r ${B}/*_test ${D}${PTEST_PATH}
    cp -r ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    # ERROR: re2-2024.03.01-r0 do_package_qa: QA Issue: /usr/lib64/re2/ptest/string_generator_test contained in package re2-ptest requires libtesting.so()(64bit), but no providers found in RDEPENDS:re2-ptest? [file-rdeps]
    cp -r ${B}/libtesting.so ${D}${PTEST_PATH}
}

# ignore .so in /usr/lib64
SOLIBS = ".so*"
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

# Don't include so files in dev package
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"

BBCLASSEXTEND = "native nativesdk"
