DESCRIPTION = "ZeroMQ looks like an embeddable networking library but acts like a concurrency framework"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=d5311495d952062e0e4fbba39cbf3de1"

PACKAGECONFIG ??= "libsodium"
PACKAGECONFIG[libsodium] = "-DWITH_LIBSODIUM=ON,-DWITH_LIBSODIUM=OFF, libsodium"

SRC_URI = "http://github.com/zeromq/libzmq/releases/download/v${PV}/zeromq-${PV}.tar.gz \
    file://0001-CMakeLists-txt-Avoid-host-specific-path-to-libsodium.patch \
    file://run-ptest \
"
SRC_URI[md5sum] = "78acc277d95e10812d71b2b3c3c3c9a9"
SRC_URI[sha256sum] = "9d9285db37ae942ed0780c016da87060497877af45094ff9e1a1ca736e3875a2"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/libzmq/releases"

inherit cmake ptest pkgconfig

EXTRA_OECMAKE = "${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTS=ON', '-DBUILD_TESTS=OFF', d)} \
                 -DCMAKE_SKIP_RPATH=ON \
"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/bin/test_* ${D}${PTEST_PATH}/tests
}

FILES_${PN}-doc += "${datadir}/zmq/*.txt"
