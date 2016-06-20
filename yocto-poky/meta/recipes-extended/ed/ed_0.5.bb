SUMMARY = "Line-oriented text editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ddd5335ef96fb858a138230af773710 \
                    file://main.c;beginline=1;endline=17;md5=36d4b85e5ae9028e918d1cc775c2475e"

PR = "r2"
SRC_URI = "${SAVANNAH_GNU_MIRROR}/ed/ed-${PV}.tar.bz2"

SRC_URI[md5sum] = "4ee21e9dcc9b5b6012c23038734e1632"
SRC_URI[sha256sum] = "edef2bbde0fbf0d88232782a0eded323f483a0519d6fde9a3b1809056fd35f3e"

inherit autotools texinfo

EXTRA_OECONF = "'CC=${CC}' 'CXX=${CXX}' 'CFLAGS=${CFLAGS}' 'CXXFLAGS=${CXXFLAGS}' 'CPPFLAGS=${CPPFLAGS}' 'LDFLAGS=${LDFLAGS}'"

CONFIGUREOPTS_remove = "--disable-dependency-tracking"
CONFIGUREOPTS_remove = "--disable-silent-rules"
EXTRA_OECONF_remove = "--disable-static"
