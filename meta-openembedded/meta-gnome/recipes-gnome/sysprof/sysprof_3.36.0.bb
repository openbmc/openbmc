SUMMARY = "System-wide Performance Profiler for Linux"
HOMEPAGE = "http://www.sysprof.com"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sysprof/sysprof-application.c;endline=17;md5=a3de8df3b0f8876dd01e1388d2d4b607"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gnome-help gettext systemd upstream-version-is-even gsettings mime mime-xdg

DEPENDS += " \
    glib-2.0-native \
    yelp-tools-native \
    libxml2-native \
    glib-2.0 \
"

SRC_URI[archive.md5sum] = "3956e82b8744715006dde59e0ce8910b"
SRC_URI[archive.sha256sum] = "8670db4dacf7b219d30c575c465b17c8ed6724dbade347f2cde9548bff039108"
SRC_URI += " \
    file://0001-sysprof-Define-NT_GNU_BUILD_ID-if-undefined.patch \
    file://0002-tests-use-G_GSIZE_FORMAT-instead-of-G_GUINT64_FORMAT.patch \
"

PACKAGECONFIG ?= "sysprofd libsysprof ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "-Denable_gtk=true,-Denable_gtk=false,gtk+3 libdazzle"
PACKAGECONFIG[sysprofd] = "-Dwith_sysprofd=bundled,-Dwith_sysprofd=none,polkit"
PACKAGECONFIG[libsysprof] = "-Dlibsysprof=true,-Dlibsysprof=false,polkit"

EXTRA_OEMESON += "-Dsystemdunitdir=${systemd_unitdir}/system"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'sysprofd', 'sysprof2.service sysprof3.service', '', d)}"

FILES_${PN} += " \
    ${datadir}/dbus-1/system-services \
    ${datadir}/dbus-1/system.d \
    ${datadir}/dbus-1/interfaces \
    ${datadir}/metainfo \
"
