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
           "

SRC_URI[md5sum] = "d2356e0fe1c0b85285d83c6b2ad51b5f"
SRC_URI[sha256sum] = "fbacf0c81e62429df3e33bda4cee38756604f18e01d977338e23306a3e3b521e"

inherit autotools
