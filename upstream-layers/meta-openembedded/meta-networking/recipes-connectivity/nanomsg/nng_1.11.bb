SUMMARY = "nanomsg-next-generation -- light-weight brokerless messaging"
DESCRIPTION = "NNG, like its predecessors nanomsg (and to some extent ZeroMQ), is a lightweight, broker-less library, offering a simple API to solve common recurring messaging problems, such as publish/subscribe, RPC-style request/reply, or service discovery."
HOMEPAGE = "https://github.com/nanomsg/nng"
SECTION = "libs/networking"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a41e579bb4326c21c774f8e51e41d8a3"

SRC_URI = "git://github.com/nanomsg/nng.git;branch=stable;protocol=https \
           file://run-ptest \
          "

SRCREV = "84aa701f42968bf536141a24e826da66963b571a"

inherit cmake pkgconfig ptest

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DNNG_ENABLE_NNGCAT=ON \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', '-DNNG_TESTS=ON', '', d)} \
                "

PACKAGECONFIG ??= ""

PACKAGECONFIG[mbedtls] = "-DNNG_ENABLE_TLS=ON,-DNNG_ENABLE_TLS=OFF,mbedtls"

do_install_ptest(){
    install -d ${D}/${PTEST_PATH}/tests
    find ${B}/tests -type f -executable -exec install {} ${D}${PTEST_PATH}/tests/ \;
}

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*"
