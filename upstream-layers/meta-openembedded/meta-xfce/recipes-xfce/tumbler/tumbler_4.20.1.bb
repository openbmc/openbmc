DESCRIPTION = "Thumbnail service implementing the thumbnail management D-Bus specification"
HOMEPAGE = "https://docs.xfce.org/xfce/tumbler/start"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "xfce4-dev-tools-native libxfce4util"

inherit xfce gtk-doc systemd

SRC_URI[sha256sum] = "87b90df8f30144a292d70889e710c8619d8b8803f0e1db3280a4293367a42eae"

INSANE_SKIP:${PN} = "dev-so"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cover-thumbnailer] = "--enable-cover-thumbnailer,--disable-cover-thumbnailer,curl gdk-pixbuf"
PACKAGECONFIG[desktop-thumbnailer] = "--enable-desktop-thumbnailer,--disable-desktop-thumbnailer,gdk-pixbuf"
PACKAGECONFIG[font-thumbnailer] = "--enable-font-thumbnailer,--disable-font-thumbnailer,freetype gdk-pixbuf"
PACKAGECONFIG[gstreamer-thumbnailer] = "--enable-gstreamer-thumbnailer,--disable-gstreamer-thumbnailer,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[jpeg-thumbnailer] = "--enable-jpeg-thumbnailer,--disable-jpeg-thumbnailer,gdk-pixbuf"
PACKAGECONFIG[odf-thumbnailer] = "--enable-odf-thumbnailer,--disable-odf-thumbnailer,gdk-pixbuf libxml2 libgsf"
PACKAGECONFIG[pixbuf-thumbnailer] = "--enable-pixbuf-thumbnailer,--disable-pixbuf-thumbnailer,gdk-pixbuf"
PACKAGECONFIG[poppler-thumbnailer] = "--enable-poppler-thumbnailer,--disable-poppler-thumbnailer,gdk-pixbuf poppler"

EXTRA_OECONF = "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen"

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
