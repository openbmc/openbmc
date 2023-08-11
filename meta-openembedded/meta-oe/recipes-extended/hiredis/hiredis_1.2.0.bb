DESCRIPTION = "Minimalistic C client library for Redis"
HOMEPAGE = "http://github.com/redis/hiredis"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d84d659a35c666d23233e54503aaea51"

SRC_URI = " \
    git://github.com/redis/hiredis;protocol=https;branch=master \
    file://run-ptest \
    "
SRCREV = "60e5075d4ac77424809f855ba3e398df7aacefe8"

S = "${WORKDIR}/git"

inherit cmake ptest

# 'testssl' is not enabled by default as redis recipe does not build with ssl support
# option 'testssl' requires 'ssl'
PACKAGECONFIG ??= "ssl ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'test testasync', '', d)}"
PACKAGECONFIG[ssl] = "-DENABLE_SSL=ON, -DENABLE_SSL=OFF, openssl"
PACKAGECONFIG[test] = "-DDISABLE_TESTS=OFF, -DDISABLE_TESTS=ON"
PACKAGECONFIG[testssl] = "-DENABLE_SSL_TESTS=ON, -DENABLE_SSL_TESTS=OFF, openssl"
PACKAGECONFIG[testasync] = "-DENABLE_ASYNC_TESTS=ON, -DENABLE_ASYNC_TESTS=OFF, libevent"

do_install_ptest() {
    install ${S}/test.sh ${D}${PTEST_PATH}/
    install ${B}/hiredis-test ${D}${PTEST_PATH}/
    if ${@bb.utils.contains('PACKAGECONFIG','testssl','true','false',d)}; then
        sed -i 's/TEST_SSL=0/TEST_SSL=1/g' ${D}${PTEST_PATH}/run-ptest
    fi
    if ${@bb.utils.contains('PACKAGECONFIG','testasync','true','false',d)}; then
        sed -i 's/TEST_ASYNC=0/TEST_ASYNC=1/g' ${D}${PTEST_PATH}/run-ptest
    fi
}

FILES:${PN}-dev += "${datadir}/hiredis_ssl ${prefix}/build"

RDEPENDS:${PN} = "redis"
RDEPENDS:${PN}-ptest = "${@bb.utils.contains('PACKAGECONFIG', 'testssl', 'openssl-bin', '', d)}"
