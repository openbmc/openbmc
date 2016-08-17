SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SECTION = "x11/libs"
DEPENDS = "intltool-native gtk+ gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

inherit gnomebase gobject-introspection
GNOME_COMPRESS_TYPE = "xz"
SRC_URI[archive.md5sum] = "f03e1139296e2a3a92e3b65a3080cd32"
SRC_URI[archive.sha256sum] = "83f732d20781fc88b22cdc6aaf2d4f388db6d3d4ff28d1a8fd45be9fb7743a9e"

do_install_append() {
    # to avoid conflicts with libwnck3 remove cmdline tools
    # if the tools are requrired add libwnck3 to your image
    rm ${D}${bindir}/wnckprop
    rm ${D}${bindir}/wnck-urgency-monitor
    rmdir ${D}${bindir}
}
