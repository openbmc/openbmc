SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
SECTION = "console/utils"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f132b4d2adfccc63da4139a609367711"

SRC_URI = "https://strace.io/files/${PV}/strace-${PV}.tar.xz \
           file://disable-git-version-gen.patch \
           file://more-robust-test-for-m32-mx32-compile-support.patch \
           file://update-gawk-paths.patch \
           file://Makefile-ptest.patch \
           file://run-ptest \
           file://0001-Fix-build-when-using-non-glibc-libc-implementation-o.patch \
           file://mips-SIGEMT.patch \
           file://0001-caps-abbrev.awk-fix-gawk-s-path.patch \
           file://0001-tests-sigaction-Check-for-mips-and-alpha-before-usin.patch \
           "

SRC_URI[md5sum] = "f2271ab0fac49ebee9cbd7f3469227cb"
SRC_URI[sha256sum] = "5bf3148dd17306a42566f7da17368fdd781afa147db05ea63a4ca2b50f58c523"

inherit autotools ptest bluetooth

RDEPENDS_${PN}-ptest += "make coreutils grep gawk sed"

PACKAGECONFIG_class-target ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"

PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,${BLUEZ}"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"

TESTDIR = "tests"

do_install_append() {
	# We don't ship strace-graph here because it needs perl
	rm ${D}${bindir}/strace-graph
}

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
	sed -i -e '/^src/s/strace.*[1-9]/ptest/' \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:${RECIPE_SYSROOT}::g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
	    -e '/^DEB_CHANGELOGTIME/d' \
	    -e '/^RPM_CHANGELOGTIME/d' \
	${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

BBCLASSEXTEND = "native"
TOOLCHAIN = "gcc"
