SUMMARY = "System-wide Performance Profiler for Linux"
HOMEPAGE = "http://www.sysprof.com"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sp-application.c;endline=17;md5=40e55577ef122c88fe20052acda64875"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gettext systemd upstream-version-is-even gsettings

DEPENDS = "glib-2.0 libxml2-native glib-2.0-native"

SRC_URI[archive.md5sum] = "80cb47906eced2e7b9976bf00deec323"
SRC_URI[archive.sha256sum] = "e90878e5a509bd79d170a7a51d47cc5508ab1363afaf0d97654373dfd9c8ba0b"
SRC_URI += " \
           file://define-NT_GNU_BUILD_ID.patch \
           file://0001-Do-not-build-anything-in-help-as-it-requires-itstool.patch \
           "

PACKAGECONFIG ?= "${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "-Denable_gtk=true,-Denable_gtk=false,gtk+3"
PACKAGECONFIG[sysprofd] = "-Dwith_sysprofd=bundled,-Dwith_sysprofd=none,polkit"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'sysprofd', 'sysprof2.service', '', d)}"

FILES_${PN} += " \
               ${datadir}/dbus-1/system-services \
               ${datadir}/dbus-1/system.d \
               ${datadir}/metainfo \
               "
