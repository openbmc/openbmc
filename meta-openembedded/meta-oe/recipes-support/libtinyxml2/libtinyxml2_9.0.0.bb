SUMMARY = "TinyXML-2 is a simple, small, efficient, C++ XML parser that can be easily integrating into other programs"
HOMEPAGE = "http://www.grinninglizard.com/tinyxml2/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=135624eef03e1f1101b9ba9ac9b5fffd"

SRCREV = "1dee28e51f9175a31955b9791c74c430fe13dc82"
SRC_URI = "git://github.com/leethomason/tinyxml2.git;branch=master;protocol=https \
           file://run-ptest"

S = "${WORKDIR}/git"

inherit meson ptest

EXTRA_OEMESON += "${@bb.utils.contains('PTEST_ENABLED', '1', '-Dtests=true', '', d)}"

CXXFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

do_install_ptest() {
    install -Dm 0755 ${B}/xmltest ${D}${PTEST_PATH}/xmltest
    install -d ${D}${PTEST_PATH}/resources/out
    install -Dm 0644 ${B}/resources/*.xml ${D}${PTEST_PATH}/resources/
}

BBCLASSEXTEND = "native"
