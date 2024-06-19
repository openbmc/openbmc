SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/librelp"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp libidn zlib"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https;branch=stable \
           file://0001-Fix-function-inline-errors-in-debug-optimization-Og.patch \
           file://0001-tests-Fix-callback-prototype.patch \
           file://0001-tcp-fix-some-compiler-warnings-with-enable-tls-opens.patch \
           file://0001-tests-Include-missing-sys-time.h.patch \
           file://0001-relp-fix-build-against-upcoming-gcc-14-Werror-calloc.patch \
           file://run-ptest \
"

SRCREV = "b421f56d9ee31a966058d23bd23c966221c91396"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

PACKAGECONFIG ?= "tls-openssl valgrind"
# Valgrind is not available for RISCV yet
PACKAGECONFIG:remove:riscv64 = "valgrind"
PACKAGECONFIG:remove:riscv32 = "valgrind"

PACKAGECONFIG[tls] = "--enable-tls,--disable-tls,gnutls nettle"
PACKAGECONFIG[tls-openssl] = "--enable-tls-openssl,--disable-tls-openssl,openssl"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,"

# For ptests, copy source tests/*.sh scripts, Makefile and 
# executables and run them with make on target.
TESTDIR = "tests"
do_compile_ptest() {
    echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${TESTDIR}/Makefile
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/${TESTDIR}

    # copy source tests/*.sh and python scripts
    cp -f ${S}/${TESTDIR}/*.sh ${S}/${TESTDIR}/*.py ${D}${PTEST_PATH}/${TESTDIR}
    # install data files needed by the test scripts on the target
    cp -f ${S}/${TESTDIR}/*.supp ${D}${PTEST_PATH}/${TESTDIR}
    cp -rf ${S}/${TESTDIR}/tls-certs ${D}${PTEST_PATH}/${TESTDIR}

    # copy executables
    find ${B}/${TESTDIR} -type f -executable -exec cp {} ${D}${PTEST_PATH}/${TESTDIR} \;
    cp -rf ${B}/${TESTDIR}/.libs ${D}${PTEST_PATH}/${TESTDIR}
    # copy Makefile
    # run-ptest will run make which runs the executables
    cp -f ${B}/${TESTDIR}/Makefile ${D}${PTEST_PATH}/${TESTDIR}
    cp -f ${B}/${TESTDIR}/set-envvars ${D}${PTEST_PATH}/${TESTDIR}

    # give permissions to all users
    # some tests need to write to this directory
    chmod 777 -R ${D}${PTEST_PATH}/${TESTDIR}

    # do NOT need to rebuild Makefile or $(check_PROGRAMS)
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
    sed -i 's/^check-TESTS:.*$/check-TESTS:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # fix the srcdir, top_srcdir, abs_top_builddir
    sed -i 's,^\(srcdir = \).*,\1${PTEST_PATH}/${TESTDIR},' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
    sed -i 's,^\(top_srcdir = \).*,\1${PTEST_PATH}/${TESTDIR},' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
    sed -i 's,^\(abs_top_builddir = \).*,\1${PTEST_PATH}/,' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # install test-driver
    install -m 644 ${S}/test-driver ${D}${PTEST_PATH}

    # fix the python3 path for tests/set-envar
    sed -i -e s:${HOSTTOOLS_DIR}:${bindir}:g ${D}${PTEST_PATH}/${TESTDIR}/set-envvars

    # these 2 scripts need help finding their /usr/lib/librelp/ptest/tests/.libs libraries
    sed -i 's:${B}/src:${PTEST_PATH}/${TESTDIR}:' ${D}${PTEST_PATH}/${TESTDIR}/send
    sed -i 's:${B}/src:${PTEST_PATH}/${TESTDIR}:' ${D}${PTEST_PATH}/${TESTDIR}/receive
}

RDEPENDS:${PN}-ptest += "\
  make bash coreutils libgcc util-linux gawk grep \
  python3-core python3-io \
"
RRECOMMENDS:${PN}-ptest += "${@bb.utils.filter('PACKAGECONFIG', 'valgrind', d)}"

