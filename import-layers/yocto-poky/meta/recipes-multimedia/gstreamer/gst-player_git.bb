SUMMARY = "GStreamer playback helper library and examples"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://gtk/gtk-play.c;beginline=1;endline=20;md5=f8c72dae3d36823ec716a9ebcae593b9"

DEPENDS = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gtk+3"

SRC_URI = "git://github.com/sdroege/gst-player.git \
           file://filechooser.patch;apply=0 \
           file://Fix-pause-play.patch;apply=0 \
           file://Add-error-signal-emission-for-missing-plugins.patch;apply=0 \
           file://0001-gtk-play-provide-similar-behaviour-for-quit-and-clos.patch \
           file://gst-player.desktop"

SRCREV = "ea90e63c1064503f9ba5d59aa4ca604f13ca5def"
PV = "0.0.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

do_configure_prepend() {
	touch ${S}/ChangeLog
}

do_install_append() {
	install -m 0644 -D ${WORKDIR}/gst-player.desktop ${D}${datadir}/applications/gst-player.desktop
}

RDEPENDS_${PN} = "gstreamer1.0-plugins-base-playback"
RRECOMMENDS_${PN} = "gstreamer1.0-plugins-base-meta \
                     gstreamer1.0-plugins-good-meta \
                     gstreamer1.0-plugins-bad-meta \
                      ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-libav", "", d)} \
                     ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-plugins-ugly-meta", "", d)}"
RPROVIDES_${PN} += "${PN}-bin"
