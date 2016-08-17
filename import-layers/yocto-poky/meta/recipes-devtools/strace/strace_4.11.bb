SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.sourceforge.net"
SECTION = "console/utils"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=124500c21e856f0912df29295ba104c7"

SRC_URI = "${SOURCEFORGE_MIRROR}/strace/strace-${PV}.tar.xz \
           file://disable-git-version-gen.patch \
           file://more-robust-test-for-m32-mx32-compile-support.patch \
           file://update-gawk-paths.patch \
           file://use-asm-sgidefs.h.patch \
           file://0001-arc-metag-nios2-or1k-tile-fix-build.patch \
           file://Makefile-ptest.patch \
           file://0001-tests-scm_rights.c-use-libtests.patch \
           file://0001-scm_rights-fd.test-rewrite-without-fork.patch \
           file://0001-Move-gcc-compat-macros-to-gcc_compat.h.patch \
           file://0001-tests-introduce-libtests.patch \
           file://run-ptest \
          "

SRC_URI[md5sum] = "a15d2555a7febb56d00c6e1a51c655dc"
SRC_URI[sha256sum] = "e86a5f6cd8f941f67f3e4b28f4e60f3d9185c951cf266404533210a2e5cd8152"

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
	sed -i -e '/^src/s/strace.*[1-9]/ptest/' ${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

BBCLASSEXTEND = "native"
