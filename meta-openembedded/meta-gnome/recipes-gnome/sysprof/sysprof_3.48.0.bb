SUMMARY = "System-wide Performance Profiler for Linux"
HOMEPAGE = "http://www.sysprof.com"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sysprof/sysprof-application.c;endline=17;md5=a3de8df3b0f8876dd01e1388d2d4b607"

inherit gnomebase gnome-help gettext systemd upstream-version-is-even gsettings mime mime-xdg

DEPENDS += " \
    glib-2.0-native \
    yelp-tools-native \
    libxml2-native \
    glib-2.0 \
    json-glib \
"

SRC_URI += "file://0001-meson-Check-for-libunwind-instead-of-libunwind-gener.patch \
            file://0002-meson-Do-not-invoke-the-commands-to-update-the-icon-.patch \
            file://0001-libsysprof-Check-for-unw_set_caching_policy-before-u.patch \
            "
SRC_URI[archive.sha256sum] = "07d9081a66cf2fb52753f48ff2b85ada75c60ff1bc1af1bd14d8aeb627972168"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'sysprofd', '', d)} \
                  ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)} \
                  agent \
                  libsysprof \
                  libunwind \
                  "
# nongnu libunwind needs porting to RV32
PACKAGECONFIG:remove:riscv32 = "libunwind"

PACKAGECONFIG[gtk] = "-Dgtk=true,-Dgtk=false,gtk4 libadwaita"
PACKAGECONFIG[sysprofd] = "-Dsysprofd=bundled,-Dsysprofd=none,polkit"
PACKAGECONFIG[libsysprof] = "-Dlibsysprof=true,-Dlibsysprof=false,json-glib"
PACKAGECONFIG[libunwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"
PACKAGECONFIG[agent] = "-Dagent=true,-Dagent=false,"

EXTRA_OEMESON += "-Dsystemdunitdir=${systemd_unitdir}/system"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'sysprofd', 'sysprof2.service sysprof3.service', '', d)}"

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services \
    ${datadir}/dbus-1/system.d \
    ${datadir}/dbus-1/interfaces \
    ${datadir}/metainfo \
"
