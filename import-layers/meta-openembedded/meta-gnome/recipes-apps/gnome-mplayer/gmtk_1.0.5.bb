SUMMARY = "GTK+ widget and function libraries for gnome-mplayer"
HOMEPAGE = "http://code.google.com/p/gmtk"
SECTION = "libs"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+ alsa-lib glib-2.0 virtual/libx11 intltool-native pulseaudio gtk+3"

SRC_URI = "http://${BPN}.googlecode.com/files/${BP}.tar.gz"
SRC_URI[md5sum] = "e06e9ca8d61d74910343bb3ef4348f7f"
SRC_URI[sha256sum] = "a07130d62719e8c1244f8405dd97445798df5204fc0f3f2f2b669b125114b468"

EXTRA_OECONF = "--disable-gconf --with-gio --with-alsa --enable-keystore"

inherit gettext pkgconfig autotools gconf
