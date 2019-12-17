SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "intltool-native gnome-common-native gtk+3 gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=enabled,-Dstartup_notification=disabled,startup-notification"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

inherit gnomebase gobject-introspection gtk-doc gettext features_check

SRC_URI[archive.md5sum] = "89dbe5a1843fd3745b8b64b34a2ef55d"
SRC_URI[archive.sha256sum] = "9595835cf28d0fc6af5526a18f77f2fcf3ca8c09e36741bb33915b6e69b8e3ca"

# libxres means x11 only
REQUIRED_DISTRO_FEATURES = "x11"
