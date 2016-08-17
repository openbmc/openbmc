DESCRIPTION = "GNU Scientific Library"
HOMEPAGE = "http://www.gnu.org/software/gsl/"
SECTION = "libs"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools pkgconfig binconfig

LEAD_SONAME = "libgsl.so"
