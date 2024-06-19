SUMMARY = "Musl libc unit tests"
HOMEPAGE = "https://wiki.musl-libc.org/libc-test.html"
DESCRIPTION = "libc-test is a collection of unit tests to measure the \
correctness and robustness of a C/POSIX standard library implementation. It is \
developed as part of the musl project."
SECTION = "tests"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=43ed1245085be90dc934288117d55a3b"

inherit ptest

SRCREV = "18e28496adee3d84fefdda6efcb9c5b8996a2398"
SRC_URI = " \
    git://repo.or.cz/libc-test;branch=master;protocol=https \
    file://run-ptest \
    file://run-libc-ptests \
"

PV = "0+git"

S = "${WORKDIR}/git"

# libc-test 'make' or 'make run' command is designed to build and run tests. It
# reports both build and test failures. The commands should be run on target.
do_compile() {
    :
}

RDEPENDS:${PN} = " \
    bash \
    grep \
    musl \
    packagegroup-core-buildessential \
"

RDEPENDS:${PN}-ptest = " \
     ${PN} \
     musl-staticdev \
     sed \
"

install_path = "/opt/${PN}"
FILES:${PN} += "${install_path}/*"

do_install () {
    install -d ${D}${install_path}/
    cp ${S}/Makefile ${D}${install_path}
    cp ${S}/config.mak.def ${D}${install_path}/config.mak
    cp -r ${S}/src ${D}${install_path}
}

do_install_ptest_base:append() {
    install -Dm 0755 ${UNPACKDIR}/run-libc-ptests ${D}${PTEST_PATH}/run-libc-ptests
}

COMPATIBLE_HOST = "null"
COMPATIBLE_HOST:libc-musl = "(.*)"
