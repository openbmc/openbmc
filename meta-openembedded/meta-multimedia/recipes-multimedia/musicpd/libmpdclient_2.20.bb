SUMMARY = "C client library for the Music Player Daemon"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=06b9dfd2f197dc514d8ef06549684b77"
HOMEPAGE = "https://www.musicpd.org/libs/libmpdclient/"

inherit meson

SRC_URI = " \
    git://github.com/MusicPlayerDaemon/libmpdclient;branch=master;protocol=https \
"
SRCREV = "7124a0ad4841a44db084bb785a6e7120bc8f0139"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= "tcp"
PACKAGECONFIG[tcp] = "-Dtcp=true,-Dtcp=false"

do_install:append() {
    # libmpdclient's Vala bindings are outdated and unmaintained; it
    # is likely that nobody will ever use them, so let's not install
    # them
    rm -rf ${D}${datadir}/vala
}
