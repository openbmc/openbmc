SUMMARY = "TinyXML-2 is a simple, small, efficient, C++ XML parser that can be easily integrating into other programs"
HOMEPAGE = "https://leethomason.github.io/tinyxml2"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=135624eef03e1f1101b9ba9ac9b5fffd"

SRCREV = "321ea883b7190d4e85cae5512a12e5eaa8f8731f"
SRC_URI = "git://github.com/leethomason/tinyxml2.git;branch=master;protocol=https \
           file://run-ptest"

S = "${WORKDIR}/git"

inherit meson ptest

EXTRA_OEMESON += " \
    ${@bb.utils.contains('PTEST_ENABLED', '1', '-Dtests=true', '', d)} \
    -Ddefault_library=both \
"

CXXFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

do_install_ptest() {
    install -Dm 0755 ${B}/xmltest ${D}${PTEST_PATH}/xmltest
    install -d ${D}${PTEST_PATH}/resources/out
    for f in ${S}/resources/*.xml; do
        install -m 0644 $f ${D}${PTEST_PATH}/resources/
    done
}

BBCLASSEXTEND = "native nativesdk"
