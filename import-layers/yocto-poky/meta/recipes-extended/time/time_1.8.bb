SUMMARY = "Tool that measures CPU resources"
DESCRIPTION = "time measures many of the CPU resources, such as time and \
memory, that other programs use."
HOMEPAGE = "http://www.gnu.org/software/time/"
SECTION = "utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit texinfo update-alternatives

ALTERNATIVE_${PN} = "time"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${GNU_MIRROR}/time/time-${PV}.tar.gz"

SRC_URI[md5sum] = "4e00dcb8c3ab11c7cf5a0d698828ac96"
SRC_URI[sha256sum] = "8a2f540155961a35ba9b84aec5e77e3ae36c74cecb4484db455960601b7a2e1b"

inherit autotools
