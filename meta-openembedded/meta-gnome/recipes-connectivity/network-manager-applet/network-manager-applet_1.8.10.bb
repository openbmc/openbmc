SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libnotify libsecret networkmanager dbus-glib \
           gconf libgnome-keyring iso-codes nss \
           intltool-native \
"

inherit distro_features_check gnomebase gsettings gtk-icon-cache gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "eae3be75e77ff1a7ea3174be25e62d03"
SRC_URI[archive.sha256sum] = "0adc4bfae8b49f7a1d929c22ef20933bd41fb4a8b458280f44c65f9e45b4c9c3"

PACKAGECONFIG[modemmanager] = "--with-wwan,--without-wwan,modemmanager"
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
    ${datadir}/appdata \
    ${datadir}/nm-applet/ \
    ${datadir}/libnm-gtk/wifi.ui \
    ${datadir}/libnma/wifi.ui \
"
