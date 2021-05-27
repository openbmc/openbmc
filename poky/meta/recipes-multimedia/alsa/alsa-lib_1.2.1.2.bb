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

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2 \
           file://0001-configure.ac-remove-an-unnecessary-libtool-fix.patch \
           file://0001-ucm-Use-strncmp-to-avoid-access-out-of-boundary.patch \
           file://0002-ucm-return-always-at-least-NULL-if-no-list-is-availa.patch \
           file://0003-namehint-correct-the-args-check.patch \
           file://0004-namehint-improve-the-previous-patch-check-the-return.patch \
           file://0005-ucm-Do-not-fail-to-parse-configs-on-cards-with-an-em.patch \
           file://0001-Fix-alsa-sound-.h-for-external-programs.patch \
           file://0001-uapi-Move-typedefs-from-uapi-to-sound.patch \
           "
SRC_URI[md5sum] = "82ddd3698469beec147e4f4a67134ea0"
SRC_URI[sha256sum] = "958e260e3673f1f6ff6b2d2c0df3fc2e469bea5b2957163ce96ce17f23e87943"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    ${@bb.utils.contains('TARGET_FPU', 'soft', '--with-softfloat', '', d)} \
    --disable-python \
"

PACKAGES =+ "alsa-server alsa-conf libatopology"

FILES_alsa-server = "${bindir}/*"
FILES_alsa-conf = "${datadir}/alsa/"
FILES_libatopology = "${libdir}/libatopology.so.*"

RDEPENDS_${PN}_class-target = "alsa-conf alsa-ucm-conf"
RDEPENDS_libatopology_class-target = "alsa-topology-conf"

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
