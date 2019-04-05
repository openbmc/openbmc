SUMMARY = "ALSA sound library"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7 \
                    file://src/socket.c;md5=dd1bc7f44872690224d89c1a9806e495;beginline=1;endline=26 \
                    "

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[md5sum] = "0eec0d657a07927795809c8f87fb76ca"
SRC_URI[sha256sum] = "3cdc3a93a6427a26d8efab4ada2152e64dd89140d981f6ffa003e85be707aedf"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    ${@bb.utils.contains('TARGET_FPU', 'soft', '--with-softfloat', '', d)} \
    --disable-python \
"

PACKAGES =+ "alsa-server alsa-conf alsa-doc"

FILES_alsa-server = "${bindir}/*"
FILES_alsa-conf = "${datadir}/alsa/"

RDEPENDS_${PN}_class-target = "alsa-conf"

# upgrade path
RPROVIDES_${PN} = "libasound"
RREPLACES_${PN} = "libasound"
RCONFLICTS_${PN} = "libasound"

RPROVIDES_${PN}-dev = "alsa-dev"
RREPLACES_${PN}-dev = "alsa-dev"
RCONFLICTS_${PN}-dev = "alsa-dev"

RPROVIDES_alsa-conf = "alsa-conf-base"
RREPLACES_alsa-conf = "alsa-conf-base"
RCONFLICTS_alsa-conf = "alsa-conf-base"

BBCLASSEXTEND = "native nativesdk"
