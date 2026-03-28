SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

GNOMEBN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "cairo glib-2.0 gtk+3"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "-Dstartup_notification=enabled,-Dstartup_notification=disabled,startup-notification libxres"

GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

inherit gnomebase gobject-introspection gtk-doc gettext features_check

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI += "file://0001-Fix-build-issue-caused-by-OE-core-changes-to-startup.patch;patch=1"
SRC_URI[archive.sha256sum] = "6af8ac41a8f067ade1d3caaed254a83423b5f61ad3f7a460fcacbac2e192bdf7"

REQUIRED_DISTRO_FEATURES = "x11"
