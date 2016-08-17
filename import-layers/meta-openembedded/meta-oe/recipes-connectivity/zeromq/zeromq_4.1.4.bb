DESCRIPTION = "ZeroMQ looks like an embeddable networking library but acts like a concurrency framework"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=d5311495d952062e0e4fbba39cbf3de1"
DEPENDS = "libsodium"

SRC_URI = "http://download.zeromq.org/zeromq-${PV}.tar.gz \
           file://run-ptest \
           "
SRC_URI[md5sum] = "a611ecc93fffeb6d058c0e6edf4ad4fb"
SRC_URI[sha256sum] = "e99f44fde25c2e4cb84ce440f87ca7d3fe3271c2b8cfbc67d55e4de25e6fe378"

S = "${WORKDIR}/zeromq-${PV}"

#Uncomment to choose polling system manually. valid values are kqueue, epoll, devpoll, poll or select
#EXTRA_OECONF += "--with-poller=kqueue"
#CFLAGS_append += "-O0"
#CXXFLAGS_append += "-O0"

inherit autotools ptest pkgconfig

do_compile_ptest () {
	echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${B}/Makefile
	oe_runmake buildtest-TESTS
}

do_install_ptest () {
        install -d ${D}${PTEST_PATH}/tests
        install -m 0755 ${B}/.libs/test_* ${D}${PTEST_PATH}/tests
}
