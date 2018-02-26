SUMMARY = "Evince is a document viewer for document formats like pdf, ps, djvu"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=96f2f8d5ee576a2163977938ea36fa7b"
SECTION = "x11/office"
DEPENDS = "gtk+3 libsecret ${@bb.utils.contains('DISTRO_FEATURES','x11','gnome-desktop3','',d)} poppler gstreamer1.0-plugins-base orc adwaita-icon-theme intltool-native gnome-common-native"
PR = "r5"

inherit gnome pkgconfig gtk-icon-cache gsettings gobject-introspection distro_features_check systemd

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0001-help-remove-YELP-macro.patch \
           file://0001-Add-format-attribute-to-_synctex_malloc.patch \
           "
SRC_URI[archive.md5sum] = "3fb65ff46de191dc49c481f1fc66201c"
SRC_URI[archive.sha256sum] = "043895af7bbd6f1b57f9ab8778e78cf9c0af5dfcc347eaa94a17bf864c04dc8f"

EXTRA_OECONF = " --enable-thumbnailer \
"

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}


do_install_append() {
    install -d ${D}${datadir}/pixmaps
    install -m 0755 ${S}/data/icons/48x48/apps/evince.png ${D}${datadir}/pixmaps/
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}
    then
        install -d ${D}${systemd_unitdir}/system
        mv ${D}${systemd_user_unitdir}/evince.service ${D}${systemd_system_unitdir}/evince.service
    else
        rm -rf ${D}${libdir}/systemd/user/evince.service
    fi
    rmdir --ignore-fail-on-non-empty ${D}${systemd_user_unitdir}
    rmdir --ignore-fail-on-non-empty ${D}${nonarch_libdir}/systemd
    rmdir --ignore-fail-on-non-empty ${D}${nonarch_libdir}
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "--enable-nautilus,--disable-nautilus,nautilus"
PACKAGECONFIG[browser-plugin] = "--enable-browser-plugin,--disable-browser-plugin,"

SYSTEMD_SERVICE_${PN} = "evince.service"

RDEPENDS_${PN} += "glib-2.0-utils"
RRECOMMMENDS_${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"
PACKAGES =+ "${PN}-browser-plugin"

FILES_${PN} += "${datadir}/dbus-1 \
                ${datadir}/appdata \
                ${datadir}/thumbnailers \
                ${systemd_unitdir}/systemd/user/evince.service \
               "
FILES_${PN}-dbg += "${libdir}/*/*/.debug \
                    ${libdir}/*/*/*/.debug"
FILES_${PN}-dev += "${libdir}/nautilus/extensions-2.0/*.la \
                    ${libdir}/evince/*/backends/*.la"
FILES_${PN}-staticdev += "${libdir}/nautilus/extensions-2.0/*.a \
                          ${libdir}/evince/*/backends/*.a"
FILES_${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"
FILES_${PN}-browser-plugin = "${libdir}/mozilla/*/*so"
