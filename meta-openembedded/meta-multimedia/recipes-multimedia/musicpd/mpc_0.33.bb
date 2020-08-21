SUMMARY = "A minimalist command line interface to the Music Player Daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE = "https://www.musicpd.org/clients/mpc/"

inherit meson

DEPENDS += "libmpdclient"

SRC_URI = "git://github.com/MusicPlayerDaemon/mpc"
SRCREV = "ef16b280052ef0320cb80f79d74c8ce0324005ed"

S = "${WORKDIR}/git"
