SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libnotify libsecret networkmanager dbus-glib gconf libgnome-keyring iso-codes nss"

inherit gnomebase gsettings gtk-icon-cache gobject-introspection

GNOME_COMPRESS_TYPE = "xz"

SRC_URI[archive.md5sum] = "86b17e1bf1a37c649874883b587c6db6"
SRC_URI[archive.sha256sum] = "b045ac3eaa68ccbbefe91510ad67b4002a7e09d1e5ce1c4bf9a67619bd2cf0eb"

PACKAGECONFIG[bluetooth] = "--with-bluetooth,--without-bluetooth,gnome-bluetooth,gnome-bluetooth"
PACKAGECONFIG[modemmanager] = "--with-modem-manager-1,--without-modem-manager-1,modemmanager"
PACKAGECONFIG ??= ""

do_configure_append() {
    # Sigh... --enable-compile-warnings=no doesn't actually turn off -Werror
    for i in $(find ${B} -name "Makefile") ; do
        sed -i -e s:-Werror::g $i
    done
}

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/nm-applet/ \
    ${datadir}/libnm-gtk/wifi.ui \
"

FILES_${PN} += "${libdir}/gnome-bluetooth/plugins/*.so"
FILES_${PN}-dev += "${libdir}/gnome-bluetooth/plugins/libnma.la"
FILES_${PN}-staticdev += "${libdir}/gnome-bluetooth/plugins/libnma.a"
FILES_${PN}-dbg += "${libdir}/gnome-bluetooth/plugins/.debug/"
