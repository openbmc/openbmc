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

SRC_URI += "file://0001-meson-Check-for-libunwind-instead-of-libunwind-gener.patch"
SRC_URI[archive.sha256sum] = "ab5d9f5b71973b3088d58a1bfdf1dc23c39a02f5fce4e5e9c73e034b178b005b"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'sysprofd libsysprof', '', d)} \
                  ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)} \
                  libunwind"
# nongnu libunwind needs porting to RV32
PACKAGECONFIG:remove:riscv32 = "libunwind"

PACKAGECONFIG[gtk] = "-Denable_gtk=true,-Denable_gtk=false,gtk+3 libdazzle"
PACKAGECONFIG[sysprofd] = "-Dwith_sysprofd=bundled,-Dwith_sysprofd=none,polkit"
PACKAGECONFIG[libsysprof] = "-Dlibsysprof=true,-Dlibsysprof=false,polkit"
PACKAGECONFIG[libunwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"

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
