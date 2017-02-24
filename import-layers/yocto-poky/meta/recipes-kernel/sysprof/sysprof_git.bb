SUMMARY = "System-wide Performance Profiler for Linux"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/sp-application.c;endline=17;md5=40e55577ef122c88fe20052acda64875"

inherit gnomebase gettext systemd

DEPENDS = "glib-2.0"

S = "${WORKDIR}/git"
SRCREV = "9c6cec9b49766bf77c1713bc5a7c6d651e628068"
PV = "3.20.0+git${SRCPV}"

SRC_URI = "git://git.gnome.org/sysprof \
           file://define-NT_GNU_BUILD_ID.patch \
           file://0001-configure-Add-option-to-enable-disable-polkit.patch \
           file://0001-Disable-check-for-polkit-for-UI.patch \
           file://0001-Avoid-building-docs.patch \
           file://0001-callgraph-Use-U64_TO_POINTER.patch \
           file://0001-Forward-port-mips-arm-memory-barrier-patches.patch \
          "
SRC_URI[archive.md5sum] = "d56e8492033b60e247634731e7f760b9"
SRC_URI[archive.sha256sum] = "4a338ad41bfffae87ef281f6e75c9660b3e0c6671bf5233be0c3f55a5e5b1ce5"

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
