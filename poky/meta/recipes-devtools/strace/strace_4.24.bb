SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
SECTION = "console/utils"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ddb91734b9c705f3e87362e97e5f64b"

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
SRC_URI[md5sum] = "8780136849c85acf76ad3a522aa4462a"
SRC_URI[sha256sum] = "1f4e59fc1edfa2bfb4adf2a748623dc25b105ec79713dd84404199f91b0b0634"

inherit autotools ptest bluetooth

PACKAGECONFIG_class-target ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"

PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,${BLUEZ}"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"

EXTRA_OECONF += "--enable-mpers=no"

CFLAGS_append_libc-musl = " -Dsigcontext_struct=sigcontext"

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
	install -m 755 ${S}/test-driver ${D}${PTEST_PATH}
	install -m 644 ${B}/config.h ${D}${PTEST_PATH}
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

RDEPENDS_${PN}-ptest += "make coreutils grep gawk sed"

BBCLASSEXTEND = "native"
TOOLCHAIN = "gcc"
