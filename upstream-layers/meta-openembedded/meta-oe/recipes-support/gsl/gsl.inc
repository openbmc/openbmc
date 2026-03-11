DESCRIPTION = "GNU Scientific Library"
HOMEPAGE = "http://www.gnu.org/software/gsl/"
SECTION = "libs"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools pkgconfig binconfig

LEAD_SONAME = "libgsl.so"

# Uses fpu_control.h which is glibc only header see ieee-utils/fp-gnuppc.c
COMPATIBLE_HOST:libc-musl:powerpc = "null"
