SUMMARY = "System-wide Performance Profiler for Linux"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sp-application.c;endline=17;md5=40e55577ef122c88fe20052acda64875"

inherit gnomebase gettext systemd upstream-version-is-even

DEPENDS = "glib-2.0 libxml2-native glib-2.0-native"

SRC_URI += " \
           file://define-NT_GNU_BUILD_ID.patch \
           file://0001-configure-Add-option-to-enable-disable-polkit.patch \
           file://0001-Disable-check-for-polkit-for-UI.patch \
           file://0001-Avoid-building-docs.patch \
          "
SRC_URI[archive.sha256sum] = "e6dca325b3014440f457a92db18ffe342a35888db3f0756694a99b9652796367"
SRC_URI[archive.md5sum] = "9514065dc752105240e5567c13708af4"

AUTOTOOLS_AUXDIR = "${S}/build-aux"

EXTRA_OECONF = "--enable-compile-warnings"

PACKAGECONFIG ?= "${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+3"
PACKAGECONFIG[polkit] = "--enable-polkit,--disable-polkit,polkit dbus"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
FILES_${PN} += "${datadir}/icons/"

SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'sysprof2.service', '', d)}"

# We do not yet work for aarch64.
COMPATIBLE_HOST = "^(?!aarch64).*"
