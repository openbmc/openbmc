SUMMARY = "GNOME wallpapers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase allarch gettext upstream-version-is-even allarch

SRC_URI[archive.sha256sum] = "f7712a873a80c9a9fcf3952611effeb2d9aed23a3e8abfcda8afb15c427d1ee3"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
