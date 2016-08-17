SUMMARY = "Matchbox Window Manager Desktop"
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://libtaku/eggsequence.h;endline=20;md5=b91f68f7642a1459fa1f4c9df94a8f15 \
                    file://src/desktop.c;endline=20;md5=36c9bf295e6007f3423095f405af5a2d \
                    file://src/main.c;endline=19;md5=2044244f97a195c25b7dc602ac7e9a00"

DEPENDS = "gtk+ startup-notification dbus"
SECTION = "x11/wm"
SRCREV = "71e3e6e04271e9d5a14f1c231ef100c7f320134d"
PV = "2.0+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/${BPN}-2 \
           file://0001-Do-nothing-on-delete-event-when-not-STANDALONE.patch \
           "

EXTRA_OECONF = "--enable-startup-notification --with-dbus"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
