SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libnotify libsecret networkmanager \
           gconf libgnome-keyring iso-codes nss \
           intltool-native \
"

GNOMEBASEBUILDCLASS = "autotools-brokensep"
inherit distro_features_check gnomebase gsettings gtk-icon-cache gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "06aafa851762146034351aa72ebb23d4"
SRC_URI[archive.sha256sum] = "050dbb155566c715239dc0378844d8beed10954e64e71014ecef8ca912df34ec"

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
    export GIR_EXTRA_LIBS_PATH="${B}/src/libnma/.libs"
}

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"

# musl builds generate gir files which otherwise go un-packaged
FILES_${PN}-dev += " \
    ${datadir}/gir-1.0 \
"
