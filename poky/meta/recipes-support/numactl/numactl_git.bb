SUMMARY = "Development package for building Applications that use numa"
HOMEPAGE = "http://oss.sgi.com/projects/libnuma/" 
DESCRIPTION = "Simple NUMA policy support. It consists of a numactl program \
to run other programs with a specific NUMA policy and a libnuma to do \
allocations with NUMA policy in applications."
LICENSE = "GPL-2.0 & LGPL-2.1"
SECTION = "apps"

inherit autotools-brokensep ptest

LIC_FILES_CHKSUM = "file://README.md;beginline=19;endline=32;md5=f8ff2391624f28e481299f3f677b21bb"

SRCREV = "dd6de072c92c892a86e18c0fd0dfa1ba57a9a05d"
PV = "2.0.14"

SRC_URI = "git://github.com/numactl/numactl \
           file://Fix-the-test-output-format.patch \
           file://Makefile \
           file://run-ptest \
           file://0001-define-run-test-target.patch \
           "

S = "${WORKDIR}/git"

LDFLAGS_append_riscv64 = " -latomic"
LDFLAGS_append_riscv32 = " -latomic"

do_install() {
    oe_runmake DESTDIR=${D} prefix=${D}/usr install
    #remove the empty man2 directory
    rm -r ${D}${mandir}/man2
}

do_compile_ptest() {
    oe_runmake test
}

do_install_ptest() {
    #install tests binaries
    local test_binaries="distance ftok mbind_mig_pages migrate_pages move_pages \
    mynode    nodemap node-parse pagesize prefered randmap realloc_test \
    tbitmap tshared"

    [ ! -d ${D}/${PTEST_PATH}/test ] && mkdir -p ${D}/${PTEST_PATH}/test
    for i in $test_binaries; do
        install -m 0755 ${B}/test/.libs/$i ${D}${PTEST_PATH}/test
    done

    local test_scripts="checktopology checkaffinity printcpu regress regress2 \
        shmtest  runltp bind_range"
    for i in $test_scripts; do
        install -m 0755 ${B}/test/$i ${D}${PTEST_PATH}/test
    done

    install -m 0755 ${WORKDIR}/Makefile ${D}${PTEST_PATH}/
    install -m 0755 ${B}/.libs/numactl ${D}${PTEST_PATH}/
}

RDEPENDS_${PN}-ptest = "bash"
