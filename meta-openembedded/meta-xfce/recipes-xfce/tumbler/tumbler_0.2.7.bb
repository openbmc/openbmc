DESCRIPTION = "Thumbnail service implementing the thumbnail management D-Bus specification"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "freetype gdk-pixbuf poppler curl xfce4-dev-tools-native libxml2 libgsf"

inherit xfce gtk-doc

SRC_URI[md5sum] = "a59b2a545c9c128dbd2b960a7779dec0"
SRC_URI[sha256sum] = "f704c35f16716cbee3c94883cee2a8d865e9cf2535d0e84f57e1c8cce00314e4"

INSANE_SKIP_${PN} = "dev-so"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gstreamer-thumbnailer] = "--enable-gstreamer-thumbnailer,--disable-gstreamer-thumbnailer,gstreamer1.0 gstreamer1.0-plugins-base"

do_install_append() {
    # Makefile seems to race on creation of symlink. So ensure creation here
    # until fixed properly
    ln -sf tumbler-xdg-cache.so ${D}${libdir}/tumbler-1/plugins/cache/tumbler-cache-plugin.so
}

FILES_${PN} += "${datadir}/dbus-1/services \
                ${libdir}/tumbler-1/tumblerd \
                ${libdir}/tumbler-1/plugins/*.so \
                ${libdir}/tumbler-1/plugins/cache/*.so \
"

FILES_${PN}-dev += "${libdir}/tumbler-1/plugins/*.la \
                    ${libdir}/tumbler-1/plugins/cache/*.la \
"
