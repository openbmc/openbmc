SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
DESCRIPTION = "strace is a diagnostic, debugging and instructional userspace utility for Linux. It is used to monitor and tamper with interactions between processes and the Linux kernel, which include system calls, signal deliveries, and changes of process state."
SECTION = "console/utils"
LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59a33f0a3e6122d67c0b3befccbdaa6b"

SRC_URI = "https://strace.io/files/${PV}/strace-${PV}.tar.xz \
           file://update-gawk-paths.patch \
           file://Makefile-ptest.patch \
           file://run-ptest \
           file://0001-caps-abbrev.awk-fix-gawk-s-path.patch \
           file://ptest-spacesave.patch \
           file://0001-strace-fix-reproducibilty-issues.patch \
           file://skip-load.patch \
           file://0001-landlock-update-expected-string.patch \
           "
SRC_URI[sha256sum] = "dc7db230ff3e57c249830ba94acab2b862da1fcaac55417e9b85041a833ca285"

inherit autotools ptest

# Not yet ported to rv32
COMPATIBLE_HOST:riscv32 = "null"

PACKAGECONFIG:class-target ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"

PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,bluez5"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"

EXTRA_OECONF += "--enable-mpers=no --disable-gcc-Werror"

CFLAGS:append:libc-musl = " -Dsigcontext_struct=sigcontext"

TESTDIR = "tests"
PTEST_BUILD_HOST_PATTERN = "^(DEB_CHANGELOGTIME|RPM_CHANGELOGTIME|WARN_CFLAGS_FOR_BUILD|LDFLAGS_FOR_BUILD)"

do_compile_ptest() {
	oe_runmake ${PARALLEL_MAKE} -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
	mkdir -p ${D}${PTEST_PATH}/build-aux
	mkdir -p ${D}${PTEST_PATH}/src
	install -m 755 ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
	install -m 644 ${B}/src/config.h ${D}${PTEST_PATH}/src/
        sed -i -e '/^src/s/strace.*[0-9]/ptest/' ${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

RDEPENDS:${PN}-ptest += "make coreutils grep gawk sed"

RDEPENDS:${PN}-ptest:append:libc-glibc = "\
     locale-base-en-us.iso-8859-1 \
"

BBCLASSEXTEND = "native"
