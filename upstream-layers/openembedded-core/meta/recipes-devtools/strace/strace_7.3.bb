SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
DESCRIPTION = "strace is a diagnostic, debugging and instructional userspace utility for Linux. It is used to monitor and tamper with interactions between processes and the Linux kernel, which include system calls, signal deliveries, and changes of process state."
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later AND LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9259131ce5420f9790214be1c20a83a9"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/strace-${PV}.tar.xz \
           file://update-gawk-paths.patch \
           file://Makefile-ptest.patch \
           file://run-ptest \
           file://ptest-spacesave.patch \
           file://0001-strace-fix-reproducibilty-issues.patch \
           file://skip-load.patch \
           file://0001-configure-Use-autoconf-macro-to-detect-largefile-sup.patch \
           file://0002-tests-Replace-off64_t-with-off_t.patch \
           file://0001-m4-Backport-ax_prog_cc_for_build.m4-macros.patch \
           file://0001-Handle-both-bpf-link-and-bpf_link-anon_inode-names.patch \
           "
SRC_URI[sha256sum] = "529634c40910895a9a1f3c5c1e65da2ca9ed72fb158dfc4bcc89efdf05e39b72"

inherit autotools github-releases ptest

# Not yet ported to rv32
COMPATIBLE_HOST:riscv32 = "null"

# bluez is not enabled by default due to build dependency creep in smaller builds
# like core-image-minimal leading to significantly more tasks being executed
PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,bluez5"
# Stack unwinders - mutually exclusive
PACKAGECONFIG[libdw]     = "--with-libdw,--without-libdw,elfutils,,,libunwind"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind,,,libdw"

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
	    -e '/^CC_FOR_BUILD/s/\s-std=gnu23//g' \
            -e '/^_hide_from_automake/d' \
	    -i ${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

# Too long without output for ptest-runner on qemuriscv64. Keyed on riscv64, as
# strace is not machine-specific, so also skipped on riscv64 hardware.
STRACE_PTEST_SKIP ?= ""
STRACE_PTEST_SKIP:riscv64 = "qual_fault-syscall"

do_install_ptest:append() {
	for t in ${STRACE_PTEST_SKIP}; do
		sed -i -e '1a . "${srcdir=.}/init.sh"\nskip_ "Too slow under QEMU on riscv64"' \
		    ${D}${PTEST_PATH}/${TESTDIR}/$t.test
	done
}

RDEPENDS:${PN}-ptest += "make coreutils grep gawk sed locale-base-en-us"

BBCLASSEXTEND = "native"
