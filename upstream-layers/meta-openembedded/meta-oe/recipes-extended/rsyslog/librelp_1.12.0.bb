SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/librelp"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp libidn zlib"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https;branch=stable \
           file://run-ptest \
"

SRCREV = "dab30db5108ef4bb5b6f9135e0428b57be7c4085"

CVE_PRODUCT = "rsyslog:librelp"

inherit autotools pkgconfig ptest

PACKAGECONFIG ?= "tls-openssl valgrind"
# Valgrind is not available for RISCV32 yet
PACKAGECONFIG:remove:riscv32 = "valgrind"
# armv4/armv5/armv6 is not in COMPATIBLE_HOST of valgrind
PACKAGECONFIG:remove:armv4 = "valgrind"
PACKAGECONFIG:remove:armv5 = "valgrind"
PACKAGECONFIG:remove:armv6 = "valgrind"

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

	sed -e '# do NOT need to rebuild Makefile or $(check_PROGRAMS)' \
		-e 's/^Makefile:.*$/Makefile:/' \
		-e 's/^check-TESTS:.*$/check-TESTS:/' \
		-e '# fix the srcdir, top_srcdir, abs_top_builddir' \
		-e 's,^\(srcdir = \).*,\1${PTEST_PATH}/${TESTDIR},' \
		-e 's,^\(top_srcdir = \).*,\1${PTEST_PATH}/${TESTDIR},' \
		-e 's,^\(abs_top_builddir = \).*,\1${PTEST_PATH}/,' \
		-e '# fix the path to test-driver' \
		-e '/^SH_LOG_DRIVER =/s/(top_srcdir)/(top_builddir)/' \
		-i ${D}${PTEST_PATH}/${TESTDIR}/Makefile

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

