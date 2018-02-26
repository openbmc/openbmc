SUMMARY = "High quality MP3 audio encoder"
HOMEPAGE = "http://lame.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=290&atid=100290"
SECTION = "console/utils"
LICENSE = "LGPLv2+"
LICENSE_FLAGS = "commercial"

DEPENDS = "ncurses gettext-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9 \
                    file://include/lame.h;beginline=1;endline=20;md5=a2258182c593c398d15a48262130a92b \
"
PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/lame/lame-${PV}.tar.gz \
           file://no-gtk1.patch \
           file://lame-3.99.5_fix_for_automake-1.12.x.patch \
           file://CVE-2017-13712.patch \
           "

SRC_URI[md5sum] = "84835b313d4a8b68f5349816d33e07ce"
SRC_URI[sha256sum] = "24346b4158e4af3bd9f2e194bb23eb473c75fb7377011523353196b19b9a23ff"

inherit autotools pkgconfig

PACKAGES += "libmp3lame libmp3lame-dev"
FILES_${PN} = "${bindir}/lame"
FILES_libmp3lame = "${libdir}/libmp3lame.so.*"
FILES_libmp3lame-dev = "${includedir} ${libdir}/*"
FILES_${PN}-dev = ""

CACHED_CONFIGUREVARS_append_x86 = " ac_cv_header_emmintrin_h=no ac_cv_header_xmmintrin_h=no"
