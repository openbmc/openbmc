SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

# intltool to provide intltool.m4 with AC_PROG_INTLTOOL
# xfce4-dev-tools-native for XDT_I18N macro and more importantly XDT_CHECK_OPTIONAL_PACKAGE
# which fixes mousepad/Makefile.am:72: error: HAVE_DBUS does not appear in AM_CONDITIONAL
DEPENDS = "gtk+ dbus dbus-glib gtksourceview2 intltool-native xfce4-dev-tools-native"

inherit xfce-app gsettings

SRC_URI[md5sum] = "fb85c23bcb096a46aee9ec22b4e4fdf5"
SRC_URI[sha256sum] = "39a7379b929d964665299c385b2cf705e32e8760698ccc34f91c990bb733518b"

# we have no gtksourceview 3.x around
EXTRA_OECONF = "--disable-gtk3"

FILES_${PN} += "${datadir}/glib-2.0/schemas"
