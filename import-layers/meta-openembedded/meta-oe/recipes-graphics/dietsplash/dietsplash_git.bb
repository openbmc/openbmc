SUMMARY = "Simple bootsplash for systemd systems"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# Really, no depends besides a C library

PV = "0.3"
PR = "r1"

SRCREV = "f7aadacbe3c19e37ea938e00a5141b577fb74a5e"
SRC_URI = "git://github.com/lucasdemarchi/dietsplash.git"

inherit autotools

S = "${WORKDIR}/git"

EXTRA_OECONF = " --with-systemdsystemunitdir=${systemd_unitdir}/system \
                 --disable-staticimages"

FILES_${PN} += "${systemd_unitdir}/system/"
