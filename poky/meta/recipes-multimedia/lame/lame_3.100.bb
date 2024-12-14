SUMMARY = "High quality MP3 audio encoder"
DESCRIPTION = "LAME is an educational tool to be used for learning about MP3 encoding."
HOMEPAGE = "https://lame.sourceforge.io/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=290&atid=100290"
SECTION = "console/utils"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9 \
                    file://include/lame.h;beginline=1;endline=20;md5=a2258182c593c398d15a48262130a92b \
"

DEPENDS = "ncurses gettext-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/lame/lame-${PV}.tar.gz \
           file://no-gtk1.patch \
           "

SRC_URI[md5sum] = "83e260acbe4389b54fe08e0bdbf7cddb"
SRC_URI[sha256sum] = "ddfe36cab873794038ae2c1210557ad34857a4b6bdc515785d1da9e175b1da1e"

inherit autotools pkgconfig sourceforge-releases

PACKAGES += "libmp3lame libmp3lame-dev"
FILES:${PN} = "${bindir}/lame"
FILES:libmp3lame = "${libdir}/libmp3lame.so.*"
FILES:libmp3lame-dev = "${includedir} ${libdir}/*"
FILES:${PN}-dev = ""
