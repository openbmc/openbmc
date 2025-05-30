SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

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

SRC_URI += "file://ef0e40d59c32d7ebeb94d242436e3144cefc174a.patch;patch=1 \
            file://0001-Fix-build-issue-caused-by-OE-core-changes-to-startup.patch;patch=1"
SRC_URI[archive.sha256sum] = "55a7444ec1fbb95c086d40967388f231b5c0bbc8cffaa086bf9290ae449e51d5"

# gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
