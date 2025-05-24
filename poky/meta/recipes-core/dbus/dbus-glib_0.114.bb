SUMMARY = "High level language (GLib) binding for D-Bus"
DESCRIPTION = "GLib bindings for the D-Bus message bus that integrate \
the D-Bus library with the GLib thread abstraction and main loop."
HOMEPAGE = "https://www.freedesktop.org/Software/dbus"
LICENSE = "(AFL-2.1 & LGPL-2.0-or-later & MIT) | (GPL-2.0-or-later & LGPL-2.0-or-later & MIT)"
LIC_FILES_CHKSUM = "file://COPYING;md5=267b04646da5ce3ea2df7a38a07c3f0b \
                    file://LICENSES/AFL-2.1.txt;md5=f3ad2f482ec639b440413665cfb9e714 \
                    file://LICENSES/GPL-2.0-or-later.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/LGPL-2.1-or-later.txt;md5=41890f71f740302b785c27661123bff5 \
                    file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663 \
                    file://dbus/dbus-glib.h;beginline=7;endline=21;md5=c374833bd817988323f3a8fda0dc7f48 \
                    "
SECTION = "base"

DEPENDS = "expat glib-2.0 virtual/libintl dbus-glib-native dbus"
DEPENDS:class-native = "glib-2.0-native dbus-native"

SRC_URI = "https://dbus.freedesktop.org/releases/dbus-glib/dbus-glib-${PV}.tar.gz \
           file://no-examples.patch \
           file://test-install-makefile.patch \
"
SRC_URI[sha256sum] = "c09c5c085b2a0e391b8ee7d783a1d63fe444e96717cc1814d61b5e8fc2827a7c"

inherit autotools pkgconfig gettext bash-completion gtk-doc

#default disable regression tests, some unit test code in non testing code
#PACKAGECONFIG:pn-${PN} = "tests" enable regression tests local.conf
PACKAGECONFIG ??= ""
PACKAGECONFIG[tests] = "--enable-tests,,,"

EXTRA_OECONF:class-target = "--with-dbus-binding-tool=${STAGING_BINDIR_NATIVE}/dbus-binding-tool"

PACKAGES += "${PN}-tests"

FILES:${PN} = "${libdir}/lib*${SOLIBS}"
FILES:${PN}-bash-completion += "${libexecdir}/dbus-bash-completion-helper"
LICENSE:${PN}-bash-completion = "GPL-2.0-or-later"
FILES:${PN}-dev += "${libdir}/dbus-1.0/include ${bindir}/dbus-glib-tool"
FILES:${PN}-dev += "${bindir}/dbus-binding-tool"

RDEPENDS:${PN}-tests += "dbus"
FILES:${PN}-tests = "${datadir}/${BPN}/tests"

BBCLASSEXTEND = "native"
