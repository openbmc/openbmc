SUMMARY = "GLIBC (GNU C Library)"
DESCRIPTION = "The GNU C Library is used as the system C library in most systems with the Linux kernel."
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"
SECTION = "libs"
LICENSE = "GPL-2.0-only & LGPL-2.1-or-later"

LIC_FILES_CHKSUM ?= "file://LICENSES;md5=df6e0948b55669789c30764c5fd9bc41 \
      file://COPYINGv2;md5=570a9b3749dd0463a1778803b12a6dce \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LESSERv2;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

CVE_PRODUCT = "glibc"

INHIBIT_DEFAULT_DEPS = "1"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"
#
# We will skip parsing glibc when target system C library selection is not glibc
# this helps in easing out parsing for non-glibc system libraries
#
COMPATIBLE_HOST:libc-musl:class-target = "null"

PV = "2.43+git"

TOOLCHAIN = "gcc"
