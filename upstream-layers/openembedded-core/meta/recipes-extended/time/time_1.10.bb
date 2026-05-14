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

SRC_URI = "${GNU_MIRROR}/time/time-${PV}.tar.gz"

SRC_URI[sha256sum] = "e8c29fb4ab599d8478e41e8618f50db8aede9c90af27d0d2ef28ae50d5de09c3"

inherit autotools

# Submitted fix: https://lists.gnu.org/archive/html/bug-time/2021-01/msg00000.html
do_configure:prepend () {
    [ ! -e ${S}/.tarball-version ] && echo ${PV} > ${S}/.tarball-version
}
