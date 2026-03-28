DESCRIPTION = "Thumbnail service implementing the thumbnail management D-Bus specification"
HOMEPAGE = "https://docs.xfce.org/xfce/tumbler/start"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "xfce4-dev-tools-native libxfce4util"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit xfce gtk-doc systemd

SRC_URI += " file://0001-Handle-cases-where-there-are-no-plugins-gracefully.patch"
SRC_URI[sha256sum] = "0f499f79a2a7ee49726a433584dd8a680d514101b72bd1b003360611ce1dc244"

INSANE_SKIP:${PN} = "dev-so"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cover-thumbnailer] = "-Dcover-thumbnailer=enabled,-Dcover-thumbnailer=disabled,curl gdk-pixbuf"
PACKAGECONFIG[desktop-thumbnailer] = "-Ddesktop-thumbnailer=enabled,-Ddesktop-thumbnailer=disabled,gdk-pixbuf"
PACKAGECONFIG[font-thumbnailer] = "-Dfont-thumbnailer=enabled,-Dfont-thumbnailer=disabled,freetype gdk-pixbuf"
PACKAGECONFIG[gstreamer-thumbnailer] = "-Dgst-thumbnailer=enabled,-Dgst-thumbnailer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[jpeg-thumbnailer] = "-Djpeg-thumbnailer=enabled,-Djpeg-thumbnailer=disabled,gdk-pixbuf"
PACKAGECONFIG[odf-thumbnailer] = "-Dodf-thumbnailer=enabled,-Dodf-thumbnailer=disabled,gdk-pixbuf libxml2 libgsf"
PACKAGECONFIG[pixbuf-thumbnailer] = "-Dpixbuf-thumbnailer=enabled,-Dpixbuf-thumbnailer=disabled,gdk-pixbuf"
PACKAGECONFIG[poppler-thumbnailer] = "-Dpoppler-thumbnailer=enabled,-Dpoppler-thumbnailer=disabled,gdk-pixbuf poppler"

do_install:append() {
    # Makefile seems to race on creation of symlink. So ensure creation here
    # until fixed properly
    ln -sf tumbler-xdg-cache.so ${D}${libdir}/tumbler-1/plugins/cache/tumbler-cache-plugin.so
}

PACKAGE_BEFORE_PN += "\
    ${PN}-cover-thumbnailer \
    ${PN}-desktop-thumbnailer \
    ${PN}-font-thumbnailer \
    ${PN}-gstreamer-thumbnailer \
    ${PN}-jpeg-thumbnailer \
    ${PN}-odf-thumbnailer \
    ${PN}-pixbuf-thumbnailer \
    ${PN}-poppler-thumbnailer \
"

FILES:${PN} += "${datadir}/dbus-1/services \
                ${libdir}/tumbler-1/tumblerd \
                ${libdir}/tumbler-1/plugins/*.so \
                ${libdir}/tumbler-1/plugins/cache/*.so \
                ${systemd_user_unitdir}/tumblerd.service \
"

FILES:${PN}-cover-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-cover-thumbnailer.so"
FILES:${PN}-desktop-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-desktop-thumbnailer.so"
FILES:${PN}-font-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-font-thumbnailer.so"
FILES:${PN}-gstreamer-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-gst-thumbnailer.so"
FILES:${PN}-jpeg-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-jpeg-thumbnailer.so"
FILES:${PN}-odf-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-odf-thumbnailer.so"
FILES:${PN}-pixbuf-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-pixbuf-thumbnailer.so"
FILES:${PN}-poppler-thumbnailer += "${libdir}/tumbler-1/plugins/tumbler-poppler-thumbnailer.so"

FILES:${PN}-dev += "${libdir}/tumbler-1/plugins/*.la \
                    ${libdir}/tumbler-1/plugins/cache/*.la \
"
