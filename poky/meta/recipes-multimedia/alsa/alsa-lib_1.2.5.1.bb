SUMMARY = "ALSA sound library"
DESCRIPTION = "(Occasionally a.k.a. libasound) is a userspace library that \
provides a level of abstraction over the /dev interfaces provided by the kernel modules."
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7 \
                    file://src/socket.c;md5=285675b45e83f571c6a957fe4ab79c93;beginline=9;endline=24 \
                    "

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "628421d950cecaf234de3f899d520c0a6923313c964ad751ffac081df331438e"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    ${@bb.utils.contains('TARGET_FPU', 'soft', '--with-softfloat', '', d)} \
    --disable-python \
"

PACKAGES =+ "alsa-server alsa-conf libatopology"

FILES:alsa-server = "${bindir}/*"
FILES:alsa-conf = "${datadir}/alsa/"
FILES:libatopology = "${libdir}/libatopology.so.*"

RDEPENDS:${PN}:class-target = "alsa-conf alsa-ucm-conf"
RDEPENDS:libatopology:class-target = "alsa-topology-conf"

# upgrade path
RPROVIDES:${PN} = "libasound"
RREPLACES:${PN} = "libasound"
RCONFLICTS:${PN} = "libasound"

RPROVIDES:${PN}-dev = "alsa-dev"
RREPLACES:${PN}-dev = "alsa-dev"
RCONFLICTS:${PN}-dev = "alsa-dev"

RPROVIDES:alsa-conf = "alsa-conf-base"
RREPLACES:alsa-conf = "alsa-conf-base"
RCONFLICTS:alsa-conf = "alsa-conf-base"

BBCLASSEXTEND = "native nativesdk"
