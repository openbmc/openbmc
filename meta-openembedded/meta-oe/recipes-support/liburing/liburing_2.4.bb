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
           file://0001-Fixes-build-failure-on-.-configure-make-shuffle-2836.patch"
SRC_URI:append:libc-musl:riscv64 = " file://0001-do-not-build-examples.patch "
SRC_URI:append:libc-musl:riscv32 = " file://0001-do-not-build-examples.patch "
SRCREV = "298c083d75ecde5a8833366167b3b6abff0c8d39"

S = "${WORKDIR}/git"

DEPENDS:append:libc-musl = " libucontext"
XCFLAGS = "-pthread"
XCFLAGS:append:libc-musl = " -lucontext"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR' 'LDFLAGS=${LDFLAGS}' 'XCFLAGS=${XCFLAGS}' 'BUILDDIR=${S}'"
do_configure() {
    ${S}/configure --prefix=${prefix} --libdir=${libdir} --libdevdir=${libdir} --mandir=${mandir} --datadir=${datadir} --includedir=${includedir}
}
do_install () {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native nativesdk"
