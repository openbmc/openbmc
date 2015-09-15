SUMMARY = "Simple GTK+ based panel for handheld devices"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://matchbox-panel/mb-panel.h;endline=10;md5=0b7db28f4b6863fb853d0467e590019a \
                    file://applets/startup/startup.c;endline=22;md5=b0a64fbef3097d79f8264e6907a98f03"

DEPENDS = "gnome-common gtk+ startup-notification dbus dbus-glib"
DEPENDS += " ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "libacpi", "",d)}"
DEPENDS += " ${@bb.utils.contains("MACHINE_FEATURES", "apm", "apmd", "",d)}"

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRCREV = "26a3a67b41c50e0ae163d8fe86ccf7a0f0a671ae"
PV = "2.0+git${SRCPV}"

RPROVIDES_${PN} = "matchbox-panel"
RREPLACES_${PN} = "matchbox-panel"
RCONFLICTS_${PN} = "matchbox-panel"

SRC_URI = "git://git.yoctoproject.org/${BPN} \
           file://silence-warnings.patch"

EXTRA_OECONF = "--enable-startup-notification --enable-dbus"
EXTRA_OECONF += " ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "--with-battery=acpi", "",d)}"
EXTRA_OECONF += " ${@bb.utils.contains("MACHINE_FEATURES", "apm", "--with-battery=apm", "",d)}"

S = "${WORKDIR}/git"

FILES_${PN} += "${libdir}/matchbox-panel/*.so \
                ${datadir}/matchbox-panel/brightness/*.png \
                ${datadir}/matchbox-panel/startup/*.png "
FILES_${PN}-dbg += "${libdir}/matchbox-panel/.debug"
FILES_${PN}-dev += "${libdir}/matchbox-panel/*.la"

inherit autotools pkgconfig distro_features_check
