DESCRIPTION = "ZeroMQ looks like an embeddable networking library but acts like a concurrency framework"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=d5311495d952062e0e4fbba39cbf3de1"

PACKAGECONFIG ??= "libsodium"
PACKAGECONFIG[libsodium] = "--with-libsodium, --without-libsodium, libsodium"

SRC_URI = "http://github.com/zeromq/zeromq4-1/releases/download/v${PV}/zeromq-${PV}.tar.gz \
    file://run-ptest \
"
SRC_URI[md5sum] = "c89db4dbc0b90c34c9f4983cbff6d321"
SRC_URI[sha256sum] = "02ebf60a43011e770799336365bcbce2eb85569e9b5f52aa0d8cc04672438a0a"

S = "${WORKDIR}/zeromq-${PV}"

#Uncomment to choose polling system manually. valid values are kqueue, epoll, devpoll, poll or select
#EXTRA_OECONF += "--with-poller=kqueue"
#CFLAGS_append = " -O0"
#CXXFLAGS_append = " -O0"

inherit autotools ptest pkgconfig

do_compile_ptest () {
    echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${B}/Makefile
    oe_runmake buildtest-TESTS
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/.libs/test_* ${D}${PTEST_PATH}/tests
}
