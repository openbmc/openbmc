SUMMARY = "System-wide Performance Profiler for Linux"
HOMEPAGE = "http://www.sysprof.com"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sysprof/sysprof-application.c;endline=17;md5=a3de8df3b0f8876dd01e1388d2d4b607"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gettext systemd upstream-version-is-even gsettings mime mime-xdg

DEPENDS += "glib-2.0 libxml2-native glib-2.0-native"

SRC_URI[archive.md5sum] = "cc32455277b31afb1965d627ae3e3629"
SRC_URI[archive.sha256sum] = "844bbb8d8b65071b3bca96f8e921319ceef81f2d2c51fcc9da63a4b355c893d0"
SRC_URI += "file://0001-sysprof-Define-NT_GNU_BUILD_ID-if-undefined.patch \
            file://0001-meson.build-do-not-hardcode-linux-as-host_machine-.s.patch \
            file://0001-libsysprof-ui-Rename-environ-to-sys_environ.patch \
            "

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'sysprofd', '', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'libsysprof', '', d)} \
                  ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "-Denable_gtk=true,-Denable_gtk=false,gtk+3 libdazzle"
PACKAGECONFIG[sysprofd] = "-Dwith_sysprofd=bundled,-Dwith_sysprofd=none,polkit"
PACKAGECONFIG[libsysprof] = "-Dlibsysprof=true,-Dlibsysprof=false,polkit"

# Enablig this requries yelp
EXTRA_OEMESON += "-Dhelp=false -Dsystemdunitdir=${systemd_unitdir}/system"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'sysprofd', 'sysprof2.service sysprof3.service', '', d)}"

FILES_${PN} += " \
               ${datadir}/dbus-1/system-services \
               ${datadir}/dbus-1/system.d \
               ${datadir}/dbus-1/interfaces \
               ${datadir}/metainfo \
               "
