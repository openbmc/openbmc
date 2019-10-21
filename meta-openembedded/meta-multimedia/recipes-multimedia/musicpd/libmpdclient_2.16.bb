SUMMARY = "C client library for the Music Player Daemon"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=06b9dfd2f197dc514d8ef06549684b77"
HOMEPAGE = "https://www.musicpd.org/libs/libmpdclient/"

inherit meson

SRC_URI = " \
    git://github.com/MusicPlayerDaemon/libmpdclient \
"
SRCREV = "4e8d990eb5239566ee948f1cd79b7248e008620a"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= "tcp"
PACKAGECONFIG[tcp] = "-Dtcp=true,-Dtcp=false"

do_install_append() {
    # libmpdclient's Vala bindings are outdated and unmaintained; it
    # is likely that nobody will ever use them, so let's not install
    # them
    rm -rf ${D}${datadir}/vala
}
