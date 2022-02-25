SUMMARY = "Simple GTK+ based panel for handheld devices"
DESCRIPTION = "A flexible always present 'window bar' for holding application \
launchers and small 'applet' style applications"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://matchbox-panel/mb-panel.h;endline=10;md5=0b7db28f4b6863fb853d0467e590019a \
                    file://applets/startup/startup.c;endline=22;md5=7cbcea60b667f609495222faf3e07917"

DEPENDS = "gtk+3 startup-notification dbus dbus-glib dbus-glib-native"
DEPENDS += " ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "libacpi", "",d)}"
DEPENDS += " ${@bb.utils.contains("MACHINE_FEATURES", "apm", "apmd", "",d)}"

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

# SRCREV tagged 2.11 plus some autotools fixes
SRCREV = "f82ca3f42510fb3ef10f598b393eb373a2c34ca7"

RPROVIDES:${PN} = "matchbox-panel"
RREPLACES:${PN} = "matchbox-panel"
RCONFLICTS:${PN} = "matchbox-panel"

SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master \
           file://0001-applets-systray-Allow-icons-to-be-smaller.patch \
           "

EXTRA_OECONF = "--enable-startup-notification --enable-dbus"
EXTRA_OECONF += " ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "--with-battery=acpi", "",d)}"
EXTRA_OECONF += " ${@bb.utils.contains("MACHINE_FEATURES", "apm", "--with-battery=apm", "",d)}"

S = "${WORKDIR}/git"

FILES:${PN} += "${libdir}/matchbox-panel/*.so \
                ${datadir}/matchbox-panel/brightness/*.png \
                ${datadir}/matchbox-panel/startup/*.png \
                ${datadir}/icons/"
FILES:${PN}-dev += "${libdir}/matchbox-panel/*.la"

inherit autotools pkgconfig features_check gettext
