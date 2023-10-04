SUMMARY = "Line-oriented text editor -- selftest GPL-2.0-or-later version"
HOMEPAGE = "http://www.gnu.org/software/ed/"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ddd5335ef96fb858a138230af773710 \
                    file://main.c;beginline=1;endline=17;md5=36d4b85e5ae9028e918d1cc775c2475e"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/ed/ed-${PV}.tar.bz2"

SRC_URI[md5sum] = "4ee21e9dcc9b5b6012c23038734e1632"
SRC_URI[sha256sum] = "edef2bbde0fbf0d88232782a0eded323f483a0519d6fde9a3b1809056fd35f3e"

inherit autotools texinfo

S = "${WORKDIR}/ed-${PV}"

EXTRA_OECONF = "'CC=${CC}' 'CXX=${CXX}' 'CFLAGS=${CFLAGS}' 'CXXFLAGS=${CXXFLAGS}' 'CPPFLAGS=${CPPFLAGS}' 'LDFLAGS=${LDFLAGS}'"

CONFIGUREOPTS:remove = "--disable-dependency-tracking"
CONFIGUREOPTS:remove = "--disable-silent-rules"
EXTRA_OECONF:remove = "--disable-static"

BBCLASSEXTEND = "native"
