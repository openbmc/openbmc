SUMMARY = "Simple bootsplash for systemd systems"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# Really, no depends besides a C library

PV = "0.3"
PR = "r1"

SRCREV = "ef2e1a390e768e21e6a6268977580ee129a96633"
SRC_URI = "git://github.com/lucasdemarchi/dietsplash.git \
           file://0001-configure.ac-Do-not-demand-linker-hash-style.patch \
           "

inherit autotools

S = "${WORKDIR}/git"

EXTRA_OECONF = " --with-systemdsystemunitdir=${systemd_unitdir}/system \
                 --disable-staticimages"

FILES_${PN} += "${systemd_unitdir}/system/"
