SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libnotify libsecret networkmanager dbus-glib \
           gconf libgnome-keyring iso-codes nss \
           intltool-native \
"

GNOMEBASEBUILDCLASS = "autotools-brokensep"
inherit distro_features_check gnomebase gsettings gtk-icon-cache gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "34923579b39360db64649342ee6735d8"
SRC_URI[archive.sha256sum] = "23dc1404f1e0622b7c4718b6d978b101d5e4d9be0b92133b3863a4dc29786178"

PACKAGECONFIG[modemmanager] = "--with-wwan,--without-wwan,modemmanager"
PACKAGECONFIG[mobile-provider-info] = "--enable-mobile-broadband-provider-info,--disable-mobile-broadband-provider-info,mobile-broadband-provider-info,mobile-broadband-provider-info"
PACKAGECONFIG ??= ""

EXTRA_OECONF = " \
    --without-selinux \
"

do_configure_append() {
    # Sigh... --enable-compile-warnings=no doesn't actually turn off -Werror
    for i in $(find ${B} -name "Makefile") ; do
        sed -i -e s%-Werror[^[:space:]]*%%g $i
    done
}

# gobject-introspection related
GI_DATA_ENABLED_libc-musl = "False"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/src/libnma/.libs:${B}/src/libnm-gtk/.libs"
}

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnm-gtk/wifi.ui \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"

# musl builds generate gir files which otherwise go un-packaged
FILES_${PN}-dev += " \
    ${datadir}/gir-1.0 \
"
