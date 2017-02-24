SUMMARY = "Evince is a document viewer for document formats like pdf, ps, djvu"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=96f2f8d5ee576a2163977938ea36fa7b"
SECTION = "x11/office"
DEPENDS = "gtk+3 libsecret gnome-desktop3 poppler gstreamer1.0-plugins-base orc adwaita-icon-theme intltool-native"
PR = "r5"

inherit gnome pkgconfig gtk-icon-cache gsettings gobject-introspection distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "c39af6b8b1c44d4393ef8ac9dab99c0b"
SRC_URI[archive.sha256sum] = "42ad6c7354d881a9ecab136ea84ff867acb942605bcfac48b6c12e1c2d8ecb17"

SRC_URI += "file://0001-help-remove-YELP-macro.patch \
            file://0002-fix-gcc6-errors.patch \
"

EXTRA_OECONF = " --enable-thumbnailer \
"

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}


do_install_append() {
    install -d install -d ${D}${datadir}/pixmaps
    install -m 0755 ${S}/data/icons/48x48/apps/evince.png ${D}${datadir}/pixmaps/
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "--enable-nautilus,--disable-nautilus,nautilus"
PACKAGECONFIG[browser-plugin] = "--enable-browser-plugin,--disable-browser-plugin,"

RDEPENDS_${PN} += "glib-2.0-utils"
RRECOMMMENDS_${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"
PACKAGES =+ "${PN}-browser-plugin"
FILES_${PN} += "${datadir}/dbus-1 \
                ${datadir}/appdata \
                ${datadir}/thumbnailers \
               "
FILES_${PN}-dbg += "${libdir}/*/*/.debug \
                    ${libdir}/*/*/*/.debug"
FILES_${PN}-dev += "${libdir}/nautilus/extensions-2.0/*.la \
                    ${libdir}/evince/*/backends/*.la"
FILES_${PN}-staticdev += "${libdir}/nautilus/extensions-2.0/*.a \
                          ${libdir}/evince/*/backends/*.a"
FILES_${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"
FILES_${PN}-browser-plugin = "${libdir}/mozilla/*/*so"
