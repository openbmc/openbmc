SUMMARY = "GNOME wallpapers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase allarch gettext upstream-version-is-even allarch

SRC_URI[archive.md5sum] = "1330dd3895b0cf054668a7c0db1c8487"
SRC_URI[archive.sha256sum] = "b8cb81e4cf9d085fbb23540635b492d5c124a1f8611c2aa9ac6384111d77bb0b"

FILES_${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
