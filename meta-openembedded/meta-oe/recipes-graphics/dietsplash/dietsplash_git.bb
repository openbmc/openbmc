SUMMARY = "Simple bootsplash for systemd systems"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# Really, no depends besides a C library

PV = "0.3+git"

SRCREV = "8bed71d860bbb0c8792fa2a1179c9beeae84d577"
SRC_URI = "git://github.com/lucasdemarchi/dietsplash.git;branch=master;protocol=https \
           file://0001-configure.ac-Do-not-demand-linker-hash-style.patch \
           file://0001-Mimic-GNU-basename-API-for-non-glibc-library-e.g.-mu.patch \
"

inherit autotools

S = "${WORKDIR}/git"

EXTRA_OECONF = " --with-systemdsystemunitdir=${systemd_unitdir}/system \
                 --disable-staticimages --with-rootdir=${root_prefix}"

FILES:${PN} += "${systemd_unitdir}/system/"
