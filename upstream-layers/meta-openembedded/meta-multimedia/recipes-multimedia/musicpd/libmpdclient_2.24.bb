SUMMARY = "C client library for the Music Player Daemon"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSES/BSD-2-Clause.txt;md5=63d6ee386b8aaba70b1bf15a79ca50f2 \
                    file://LICENSES/BSD-3-Clause.txt;md5=954f4d71a37096249f837652a7f586c0"
HOMEPAGE = "https://www.musicpd.org/libs/libmpdclient/"

inherit meson

SRC_URI = " \
    git://github.com/MusicPlayerDaemon/libmpdclient;branch=master;protocol=https;tag=v${PV} \
"
SRCREV = "5073329989bf52676d8c446176a1d29ed3ffccca"

PACKAGECONFIG ??= "tcp"
PACKAGECONFIG[tcp] = "-Dtcp=true,-Dtcp=false"
