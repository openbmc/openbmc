SUMMARY = "This is the io_uring library, liburing."
DESCRIPTION = "liburing provides helpers to setup and teardown io_uring \
instances, and also a simplified interface for applications that don't need \
(or want) to deal with the full kernel side implementation."
HOMEPAGE = "https://github.com/axboe/liburing"
BUGTRACKER = "https://github.com/axboe/liburing/issues"
SECTION = "libs"

LICENSE = "LGPL-2.1-only | MIT"
LIC_FILES_CHKSUM = "file://README;beginline=41;endline=44;md5=2b0e9926530c269f5ae95560370195af"

SRC_URI = "git://github.com/axboe/liburing.git;branch=master;protocol=https \
           file://0001-test-Compile-nolibc.c-only-when-CONFIG_NOLIBC-is-set.patch \
           file://0001-test-Drop-including-error.h-header.patch \
           file://0002-ooo-file-unreg.c-Include-poll.h-instead-of-sys-poll..patch"
SRCREV = "5227d48b28ad8671e61d444b72678da584d2e6c3"

S = "${WORKDIR}/git"

DEPENDS:append:libc-musl = " libucontext"
XCFLAGS = "-pthread"
XCFLAGS:append:libc-musl = " -lucontext"

USELIBC = ""
# clang-18+ on RV64 emits memset for arch/riscv64/syscall.h provided __do_syscall4 macro
# this does not happen for gcc or older clang, so link with libc since we need memset API
# -fno-builtin-memset does not help
USELIBC:riscv64:toolchain-clang = "--use-libc"
USELIBC:riscv32 = "--use-libc"
EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR' 'LDFLAGS=${LDFLAGS}' 'XCFLAGS=${XCFLAGS}' 'BUILDDIR=${S}'"
do_configure() {
    ${S}/configure --prefix=${prefix} --libdir=${libdir} --libdevdir=${libdir} --mandir=${mandir} --datadir=${datadir} --includedir=${includedir} ${USELIBC}
}
do_install () {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native nativesdk"
