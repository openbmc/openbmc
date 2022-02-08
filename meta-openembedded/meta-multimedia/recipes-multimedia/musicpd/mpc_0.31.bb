SUMMARY = "A minimalist command line interface to the Music Player Daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE = "https://www.musicpd.org/clients/mpc/"

inherit meson

DEPENDS += " \
    libmpdclient \
"

SRC_URI = " \
    git://github.com/MusicPlayerDaemon/mpc;branch=master;protocol=https \
"
SRCREV = "59875acdf34e5f0eac0c11453c49daef54f78413"
S = "${WORKDIR}/git"
