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
SRC_URI[md5sum] = "2047e917c2cc93505e2579bcba67a573"
SRC_URI[sha256sum] = "ebd7b5c830d6428956b67a0454a7f8cbed1de74b3b01e5c33c5378e22740f763"

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
