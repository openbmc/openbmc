#
# Notes on the way the OE cross toolchain now works
#
# We need a libgcc to build glibc. Tranditionally we therefore built
# a non-threaded and non-shared compiler (gcc-cross-initial), then use
# that to build libgcc-initial which is used to build glibc which we can
# then build gcc-cross and libgcc against.
#
# We were able to drop the glibc dependency from gcc-cross, with two tweaks:

# a) specify the minimum glibc version to support in a configure option
# b) create a dummy limits.h file so that later when glibc creates one,
#    the headers structure has support for it. We can do this with a simple
#    empty file
#
# Once gcc-cross is libc independent, we can use it to build both
# libgcc-initial and then later libgcc.
#
# libgcc-initial is tricky as we need to imitate the non-threaded and
# non-shared case. We can do that by hacking the threading mode back to
# "single" even if gcc reports "posix" and disable libc presence for the
# libgcc-intial build. We have to create the dummy limits.h to avoid
# compiler errors from a missing header.
#
# glibc will fail to link with libgcc-initial due to a missing "exception
# handler" capable libgcc (libgcc_eh.a). Since we know glibc doesn't need
# any exception handler, we can safely symlink to libgcc.a.
#

require libgcc-common.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc"

LICENSE = "GPL-3.0-with-GCC-exception"

PACKAGES = ""

EXTRA_OECONF += "--disable-shared"

inherit nopackages

# We really only want this built by things that need it, not any recrdeptask
deltask do_build

do_configure_prepend () {
	install -d ${STAGING_INCDIR}
	touch ${STAGING_INCDIR}/limits.h
	sed -i -e 's#INHIBIT_LIBC_CFLAGS =.*#INHIBIT_LIBC_CFLAGS = -Dinhibit_libc#' ${B}/gcc/libgcc.mvars
	sed -i -e 's#inhibit_libc = false#inhibit_libc = true#' ${B}/gcc/Makefile
}

do_configure_append () {
	sed -i -e 's#thread_header = .*#thread_header = gthr-single.h#' ${B}/${BPN}/Makefile
}

do_install_append () {
	ln -s libgcc.a ${D}${libdir}/${TARGET_SYS}/${BINV}/libgcc_eh.a
}
