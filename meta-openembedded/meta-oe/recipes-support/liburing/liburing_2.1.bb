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
           file://0001-test-Use-syscall-wrappers-instead-of-using-syscall-2.patch \
           "
SRC_URI:append:libc-musl:riscv64 = " file://0001-do-not-build-examples.patch "
SRC_URI:append:libc-musl:riscv32 = " file://0001-do-not-build-examples.patch "
SRCREV = "41a61c97c2e3df4475c93fdf5026d575ce3f1377"

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
