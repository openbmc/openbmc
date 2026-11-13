SUMMARY = "System-wide Performance Profiler for Linux"
HOMEPAGE = "http://www.sysprof.com"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sysprof/sysprof-application.c;endline=17;md5=a3de8df3b0f8876dd01e1388d2d4b607"

inherit gnomebase gnome-help gettext systemd gsettings gtk-icon-cache mime mime-xdg features_check

DEPENDS += " \
    desktop-file-utils-native \
    elfutils \
    glib-2.0 \
    glib-2.0-native \
    json-glib \
    libdex \
    libunwind \
    libxml2-native \
    yelp-tools-native \
"

SRC_URI += "file://0001-meson-Check-for-libunwind-instead-of-libunwind-gener.patch \
           file://0002-meson-Do-not-invoke-the-commands-to-update-the-icon-.patch \
           file://0003-libsysprof-Check-for-unw_set_caching_policy-before-u.patch \
           "
SRC_URI[archive.sha256sum] = "f2c8d9c5c8294ce3fdc8be7d9f4be56a72ed71522cb54987f1723a8cfd91637d"

# reason: gtk4 requires opengl distro feature
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'gtk', 'opengl', '', d)}"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'sysprofd libsysprof', '', d)} \
                  ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)} \
                 "

PACKAGECONFIG[gtk] = "-Dgtk=true,-Dgtk=false,gtk4 libpanel"
PACKAGECONFIG[sysprofd] = "-Dsysprofd=bundled,-Dsysprofd=none,polkit"
PACKAGECONFIG[libsysprof] = "-Dlibsysprof=true,-Dlibsysprof=false,polkit"

EXTRA_OEMESON += "-Dsystemdunitdir=${systemd_unitdir}/system"

SOLIBS = ".so"
FILES_SOLIBSDEV = "${libdir}/libsysprof-6.so"

SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'sysprofd', 'sysprof3.service', '', d)}"

FILES:${PN} += " \
    ${datadir}/dbus-1/services \
    ${datadir}/dbus-1/system-services \
    ${datadir}/dbus-1/system.d \
    ${datadir}/dbus-1/interfaces \
    ${datadir}/metainfo \
    ${libdir}/libsysprof-6*.so.* \
"

# Needs libunwind to be ported first
COMPATIBLE_HOST:riscv32 = "null"
