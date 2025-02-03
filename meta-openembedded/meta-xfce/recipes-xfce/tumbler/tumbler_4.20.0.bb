DESCRIPTION = "Thumbnail service implementing the thumbnail management D-Bus specification"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "freetype gdk-pixbuf poppler curl xfce4-dev-tools-native libxml2 libgsf libxfce4util"

inherit xfce gtk-doc systemd

SRC_URI[sha256sum] = "74b1647d55926547e98bfac70838ff63c5a84299a5e10c81c38d1fab90e25880"

INSANE_SKIP:${PN} = "dev-so"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gstreamer-thumbnailer] = "--enable-gstreamer-thumbnailer,--disable-gstreamer-thumbnailer,gstreamer1.0 gstreamer1.0-plugins-base"

EXTRA_OECONF = "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen"

do_install:append() {
    # Makefile seems to race on creation of symlink. So ensure creation here
    # until fixed properly
    ln -sf tumbler-xdg-cache.so ${D}${libdir}/tumbler-1/plugins/cache/tumbler-cache-plugin.so
}

FILES:${PN} += "${datadir}/dbus-1/services \
                ${libdir}/tumbler-1/tumblerd \
                ${libdir}/tumbler-1/plugins/*.so \
                ${libdir}/tumbler-1/plugins/cache/*.so \
                ${systemd_user_unitdir}/tumblerd.service \
"

FILES:${PN}-dev += "${libdir}/tumbler-1/plugins/*.la \
                    ${libdir}/tumbler-1/plugins/cache/*.la \
"
