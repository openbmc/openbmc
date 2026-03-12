SUMMARY = "nanomsg socket library"
DESCRIPTION = "nanomsg is a socket library that provides several common \
communication patterns. It aims to make the networking layer fast, scalable, \
and easy to use. Implemented in C, it works on a wide range of operating \
systems with no further dependencies."
HOMEPAGE = "https://nanomsg.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=587b3fd7fd291e418ff4d2b8f3904755"

SECTION = "libs/networking"

SRC_URI = "git://github.com/nanomsg/nanomsg.git;protocol=https;branch=master \
           file://run-ptest \
          "

SRCREV = "ccd7f20c1b756f7041598383baffcdc326246db7"

inherit cmake pkgconfig ptest

# nanomsg documentation generation requires asciidoctor,
# not asciidoc, and currently there's no asciidoctor-native
# recipe anywhere in openembedded-core or meta-openembedded
EXTRA_OECMAKE = " -DNN_ENABLE_DOC=OFF \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', '-DNN_TESTS=ON', '', d)} \
                "

# we don't want nanomsg-tools to be renamed to libnanomsg-tools
DEBIAN_NOAUTONAME:${PN}-tools = "1"

do_install_ptest(){
    install -d ${D}/${PTEST_PATH}/tests
    find ${B} -maxdepth 1 -type f -executable \
        ! -name "*_lat" \
        ! -name "*_thr" \
        ! -name "nanocat" \
        ! -name "libnanomsg.so*" \
        -exec install {} ${D}${PTEST_PATH}/tests/ \;
}

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*"
