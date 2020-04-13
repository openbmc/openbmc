SUMMARY = "High level language (GLib) binding for D-Bus"
DESCRIPTION = "GLib bindings for the D-Bus message bus that integrate \
the D-Bus library with the GLib thread abstraction and main loop."
HOMEPAGE = "https://www.freedesktop.org/Software/dbus"
LICENSE = "AFL-2.1 | GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf5b3a2f7083750d504333114e738656 \
                    file://dbus/dbus-glib.h;beginline=7;endline=21;md5=7755c9d7abccd5dbd25a6a974538bb3c"
SECTION = "base"

DEPENDS = "expat glib-2.0 virtual/libintl dbus-glib-native dbus"
DEPENDS_class-native = "glib-2.0-native dbus-native"

SRC_URI = "https://dbus.freedesktop.org/releases/dbus-glib/dbus-glib-${PV}.tar.gz \
           file://no-examples.patch \
           file://test-install-makefile.patch \
"
SRC_URI[md5sum] = "d7cebf1d69445cbd28b4983392145192"
SRC_URI[sha256sum] = "7ce4760cf66c69148f6bd6c92feaabb8812dee30846b24cd0f7395c436d7e825"

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
