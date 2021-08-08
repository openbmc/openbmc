SUMMARY = "This is the io_uring library, liburing."
DESCRIPTION = "liburing provides helpers to setup and teardown io_uring \
instances, and also a simplified interface for applications that don't need \
(or want) to deal with the full kernel side implementation."
HOMEPAGE = "https://github.com/axboe/liburing"
BUGTRACKER = "https://github.com/axboe/liburing/issues"
SECTION = "libs"

LICENSE = "LGPLv2.1 | MIT"
LIC_FILES_CHKSUM = "file://README;beginline=41;endline=44;md5=d51b5805e2a675685e6a66ca50904cf9"

SRC_URI = "git://github.com/axboe/liburing.git;branch=master;protocol=https \
           file://0001-examples-ucontext-cp.c-Do-not-use-SIGSTKSZ.patch \
           file://0001-tests-fix-portability-issue-when-using-__NR_mmap-sys.patch \
           "
SRC_URI:append:libc-musl:riscv64 = " file://0001-do-not-build-examples.patch "
SRC_URI:append:libc-musl:riscv32 = " file://0001-do-not-build-examples.patch "

SRCREV = "b013dfd5a5f65116373d5e0f0bdfb73db9d8816e"
S = "${WORKDIR}/git"

DEPENDS:append:libc-musl = " libucontext"
XCFLAGS = "-pthread"
XCFLAGS:append:libc-musl = " -lucontext"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR' 'LDFLAGS=${LDFLAGS}' 'XCFLAGS=${XCFLAGS}' 'BUILDDIR=${S}'"
do_configure() {
    ${S}/configure --prefix=${prefix}
}
do_install () {
    oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
}
