SUMMARY = "An alternative to dbug-glib."
DESCRIPTION = "An alternative to dbus-glib. It is designed specifically to \
make it comfortable to work with very large and potentially \
complex D-Bus services like e.g. DeviceKit-disks"

HOMEPAGE = "http://cgit.freedesktop.org/~david/eggdbus"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=155db86cdbafa7532b41f390409283eb \
                    file://src/eggdbus/eggdbus.h;md5=6b312aef57ed8d738b3f131ad037d6c2"

DEPENDS = "dbus glib-2.0 dbus-glib eggdbus-native"
DEPENDS_class-native = "dbus-native glib-2.0-native dbus-glib-native"

SRC_URI = "http://cgit.freedesktop.org/~david/${BPN}/snapshot/${BPN}-${PV}.tar.bz2 \
    file://gtk-doc.patch \
"
SRC_URI_append_class-target = "file://marshal.patch"
SRC_URI[md5sum] = "0a111faa54dfba2cf432c2c8e8a76e06"
SRC_URI[sha256sum] = "3ad26e271c1a879bafcd181e065fe0ed53b542299a773c3188c9edb25b895ed1"

inherit autotools gtk-doc

EXTRA_OECONF = " --disable-man-pages "

BBCLASSEXTEND = "native"

PARALLEL_MAKE = ""

do_configure_prepend() {
    # Create ChangeLog to walk around automake error.
    # " Makefile.am: error: required file './ChangeLog' not found "
    touch ${S}/ChangeLog
}
