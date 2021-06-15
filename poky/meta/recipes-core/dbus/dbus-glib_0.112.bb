SUMMARY = "High level language (GLib) binding for D-Bus"
DESCRIPTION = "GLib bindings for the D-Bus message bus that integrate \
the D-Bus library with the GLib thread abstraction and main loop."
HOMEPAGE = "https://www.freedesktop.org/Software/dbus"
LICENSE = "AFL-2.1 | GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c31c73c1d8f5d06784b2ccd22e42d641 \
                    file://dbus/dbus-glib.h;beginline=7;endline=21;md5=c374833bd817988323f3a8fda0dc7f48"
SECTION = "base"

DEPENDS = "expat glib-2.0 virtual/libintl dbus-glib-native dbus"
DEPENDS_class-native = "glib-2.0-native dbus-native"

SRC_URI = "https://dbus.freedesktop.org/releases/dbus-glib/dbus-glib-${PV}.tar.gz \
           file://no-examples.patch \
           file://test-install-makefile.patch \
"
SRC_URI[md5sum] = "021e6c8a288df02c227e4aafbf7e7527"
SRC_URI[sha256sum] = "7d550dccdfcd286e33895501829ed971eeb65c614e73aadb4a08aeef719b143a"

inherit autotools pkgconfig gettext bash-completion gtk-doc

#default disable regression tests, some unit test code in non testing code
#PACKAGECONFIG_pn-${PN} = "tests" enable regression tests local.conf
PACKAGECONFIG ??= ""
PACKAGECONFIG[tests] = "--enable-tests,,,"

EXTRA_OECONF_class-target = "--with-dbus-binding-tool=${STAGING_BINDIR_NATIVE}/dbus-binding-tool"

PACKAGES += "${PN}-tests"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"
FILES_${PN}-bash-completion += "${libexecdir}/dbus-bash-completion-helper"
FILES_${PN}-dev += "${libdir}/dbus-1.0/include ${bindir}/dbus-glib-tool"
FILES_${PN}-dev += "${bindir}/dbus-binding-tool"

RDEPENDS_${PN}-tests += "dbus-x11"
FILES_${PN}-tests = "${datadir}/${BPN}/tests"

BBCLASSEXTEND = "native"
