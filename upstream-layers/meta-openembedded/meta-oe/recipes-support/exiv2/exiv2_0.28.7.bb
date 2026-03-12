SUMMARY = "Exif, Iptc and XMP metadata manipulation library and tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "zlib expat brotli libinih"

SRC_URI = "git://github.com/Exiv2/exiv2.git;protocol=https;branch=0.28.x;tag=v${PV} \
           file://run-ptest \
           file://0001-Use-automake-output-for-tests.patch \
           file://0001-Allow-test-data-path-configuration.patch \
           "
SRCREV = "afcb7a8ba84a7de36d2f1ee7689394e078697956"

inherit cmake gettext ptest

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'test unittest', '', d)}"
PACKAGECONFIG[test] = "-DEXIV2_BUILD_SAMPLES=ON -DEXIV2_ENABLE_WEBREADY=ON"
PACKAGECONFIG[unittest] = "-DEXIV2_BUILD_UNIT_TESTS=ON -DTEST_FOLDER=${PTEST_PATH},,googletest"

RDEPENDS:${PN}-ptest += " \
    python3-html \
    python3-lxml \
    python3-multiprocessing \
    python3-shell \
    python3-unittest \
    python3-unittest-automake-output"

do_install_ptest(){
    cp -r ${S}/tests ${D}${PTEST_PATH}/
    cp -r ${S}/test ${D}${PTEST_PATH}/

    install -d ${D}${PTEST_PATH}/build/bin
    install ${B}/bin/* ${D}${PTEST_PATH}/build/bin

    install -d ${D}${PTEST_PATH}/src
    install ${S}/src/canonmn_int.cpp ${D}${PTEST_PATH}/src
}
