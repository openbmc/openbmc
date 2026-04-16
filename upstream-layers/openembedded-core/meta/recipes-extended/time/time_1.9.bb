SUMMARY = "Tool that measures CPU resources"
DESCRIPTION = "time measures many of the CPU resources, such as time and \
memory, that other programs use."
HOMEPAGE = "http://www.gnu.org/software/time/"
SECTION = "utils"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit texinfo update-alternatives

ALTERNATIVE:${PN} = "time"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${GNU_MIRROR}/time/time-${PV}.tar.gz \
           file://0001-include-string.h-for-memset.patch \
           file://0002-maint-remove-K-R-declarations.patch \
           file://0003-maint-fix-compilation-errors-with-GCC-15.patch \
           file://0004-maint-remove-obsolete-autoconf-macros.patch \
           "

SRC_URI[sha256sum] = "fbacf0c81e62429df3e33bda4cee38756604f18e01d977338e23306a3e3b521e"

inherit autotools

# Submitted fix: https://lists.gnu.org/archive/html/bug-time/2021-01/msg00000.html
do_configure:prepend () {
    [ ! -e ${S}/.tarball-version ] && echo ${PV} > ${S}/.tarball-version
}
