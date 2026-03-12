SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
DESCRIPTION = "strace is a diagnostic, debugging and instructional userspace utility for Linux. It is used to monitor and tamper with interactions between processes and the Linux kernel, which include system calls, signal deliveries, and changes of process state."
SECTION = "console/utils"
LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9259131ce5420f9790214be1c20a83a9"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/strace-${PV}.tar.xz \
           file://update-gawk-paths.patch \
           file://Makefile-ptest.patch \
           file://run-ptest \
           file://ptest-spacesave.patch \
           file://0001-strace-fix-reproducibilty-issues.patch \
           file://skip-load.patch \
           file://skip-bpf.patch \
           file://0001-configure-Use-autoconf-macro-to-detect-largefile-sup.patch \
           file://0002-tests-Replace-off64_t-with-off_t.patch \
           "
SRC_URI:append:libc-musl = "\
           file://0001-Ignore-pwritev-pwrite64-tests-on-musl.patch \
           "
SRC_URI[sha256sum] = "e076c851eec0972486ec842164fdc54547f9d17abd3d1449de8b120f5d299143"

inherit autotools github-releases ptest

# Not yet ported to rv32
COMPATIBLE_HOST:riscv32 = "null"

# bluez is not enabled by default due to build dependency creep in smaller builds
# like core-image-minimal leading to significantly more tasks being executed
PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,bluez5"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"

EXTRA_OECONF += "--enable-mpers=no --disable-gcc-Werror"

CFLAGS:append:libc-musl = " -Dsigcontext_struct=sigcontext"

TESTDIR = "tests"
PTEST_BUILD_HOST_PATTERN = "^(DEB_CHANGELOGTIME|RPM_CHANGELOGTIME|WARN_CFLAGS_FOR_BUILD|LDFLAGS_FOR_BUILD)"

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
	mkdir -p ${D}${PTEST_PATH}/build-aux
	mkdir -p ${D}${PTEST_PATH}/src
	install -m 755 ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
	install -m 644 ${B}/src/config.h ${D}${PTEST_PATH}/src/
	sed -e 's/^srcdir = .*/srcdir = ..\/..\/ptest\/tests/' \
	    -e "/^TEST_LOG_DRIVER =/s|(top_srcdir)|(top_builddir)|" \
	    -i ${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

RDEPENDS:${PN}-ptest += "make coreutils grep gawk sed locale-base-en-us"

BBCLASSEXTEND = "native"
