SUMMARY = "System-Wide Profiler"
DESCRIPTION = "OProfile is a system-wide profiler for Linux systems, capable \
of profiling all running code at low overhead."
HOMEPAGE = "http://oprofile.sourceforge.net/news/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=16191&atid=116191"

LICENSE = "LGPL-2.1-or-later & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://libopagent/opagent.h;beginline=5;endline=26;md5=4f16f72c7a493d8a4704aa18d03d15c6 \
                   "
SECTION = "devel"

DEPENDS = "popt binutils"
DEPENDS:append:powerpc64 = " libpfm4"
DEPENDS:append:powerpc64le = " libpfm4"

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://acinclude.m4 \
           file://run-ptest \
           file://0001-Fix-build-with-musl.patch \
           file://0002-Fix-configure-when-bin-sh-is-not-bash.patch \
           file://0003-Define-the-C-preprocessor-variable-to-improve-reprod.patch \
           file://0004-Use-BUILD_DATE-to-improve-reproducibility.patch \
           file://0005-Add-rmb-definition-for-NIOS2-architecture.patch \
           file://0006-replace-sym_iterator-0-with-sym_iterator.patch \
           file://0007-oprofile-doesn-t-want-GNU-levels-of-automake-strictn.patch \
           file://0008-include-linux-limits.h-for-MAX_INPUT.patch \
           file://0009-Prevent-running-check-tests-on-host-if-cross-compili.patch \
           file://0010-oprofile-Determine-the-root-home-directory-dynamical.patch \
           file://0001-configure-Include-unistd.h-for-getpid-API.patch \
           file://0001-Replace-std-bind2nd-with-generic-lambda.patch \
"
SRC_URI[sha256sum] = "7ba06f99d7c188389d20d1d5e53ee690c7733f87aa9af62bd664fa0ca235a412"

UPSTREAM_CHECK_REGEX = "oprofile-(?P<pver>\d+(\.\d+)+)/"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/oprofile/files/oprofile/"

inherit autotools pkgconfig ptest

EXTRA_OECONF = "--with-kernel=${STAGING_DIR_HOST}${prefix} --without-x ac_cv_prog_XSLTPROC="
do_configure () {
	cp ${WORKDIR}/acinclude.m4 ${S}/
	autotools_do_configure
}

EXTRA_OEMAKE = "SRCDIR=${PTEST_PATH}/libutil++/tests"
do_compile_ptest() {
	oe_runmake check
}

do_install_ptest() {
	subdirs="libdb/tests libutil++/tests libregex/tests libutil/tests libop/tests libdb/tests "
	for tooltest in ${subdirs}
	do
		find ${tooltest} -perm /u=x -type f| cpio -pvdu ${D}${PTEST_PATH}
	done

	install -d ${D}${PTEST_PATH}/../${BP}/events ${D}${PTEST_PATH}/../${BP}/libutil++/tests
	# needed by libregex regex_test
	cp libregex/stl.pat ${D}${PTEST_PATH}/libregex
	cp libregex/tests/mangled-name ${D}${PTEST_PATH}/libregex/tests

	# needed by litutil++ file_manip_tests
	cp ${S}/libutil++/tests/file_manip_tests.cpp \
		libutil++/tests/file_manip_tests.o ${D}${PTEST_PATH}/../${BP}/libutil++/tests
	cp ${S}/libutil++/tests/file_manip_tests.cpp \
		libutil++/tests/file_manip_tests.o ${D}${PTEST_PATH}/libutil++/tests
	# needed by some libop tests
	cp -R --no-dereference --preserve=mode,links -v ${S}/events ${D}${PTEST_PATH}/../${BP}
}

RDEPENDS:${PN} = "binutils-symlinks"

FILES:${PN} = "${bindir} ${libdir}/${BPN}/lib*${SOLIBS} ${datadir}/${BPN}"
FILES:${PN}-dev += "${libdir}/${BPN}/lib*${SOLIBSDEV} ${libdir}/${BPN}/lib*.la"
FILES:${PN}-staticdev += "${libdir}/${BPN}/lib*.a"
FILES:${PN}-ptest += "${libdir}/${BPN}/${BP}"
