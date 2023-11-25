DESCRIPTION = "A regular expression library"
HOMEPAGE = "https://github.com/google/re2/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b5c31eb512bdf3cb11ffd5713963760"

# tag 2023-03-01
SRCREV = "241e2e430836e80f93d704d1f06cd3e7fe3100f5"

SRC_URI = "git://github.com/google/re2.git;branch=main;protocol=https \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)} \
          "

S = "${WORKDIR}/git"

inherit cmake ptest
RDEPENDS:${PN}-ptest += "cmake sed"

EXTRA_OECMAKE += " \
	-DBUILD_SHARED_LIBS=ON \
        ${@bb.utils.contains('PTEST_ENABLED', '1', '-DRE2_BUILD_TESTING=ON', '-DRE2_BUILD_TESTING=OFF', d)} \
"

do_install_ptest () {
    cp -r ${B}/*_test ${D}${PTEST_PATH}
    cp -r ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
}

# ignore .so in /usr/lib64
FILES:${PN} = "${libdir}"
INSANE_SKIP:${PN} += "dev-so"

# Don't include so files in dev package
FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

BBCLASSEXTEND = "native nativesdk"
