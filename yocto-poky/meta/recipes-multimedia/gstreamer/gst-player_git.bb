SUMMARY = "GStreamer playback helper library and examples"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://lib/gst/player/gstplayer.c;beginline=1;endline=19;md5=03aeca9d8295f811817909075a15ff65"

DEPENDS = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base gtk+"

SRC_URI = "git://github.com/sdroege/gst-player.git \
           file://filechooser.patch \
           file://gtk2.patch \
           file://Fix-pause-play.patch \
           file://Add-error-signal-emission-for-missing-plugins.patch \
           file://gst-player.desktop"

SRCREV = "5386c5b984d40ef5434673ed62204e69aaf52645"

S = "${WORKDIR}/git"

inherit autotools gtk-doc lib_package pkgconfig distro_features_check gobject-introspection

ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

do_configure_prepend() {
	touch ${S}/ChangeLog
}

EXTRA_OECONF += "ac_cv_path_VALGRIND=no ac_cv_path_GDB=no"

do_install_append() {
	install -m 0644 -D ${WORKDIR}/gst-player.desktop ${D}${datadir}/applications/gst-player.desktop
}

FILES_${PN}-bin += "${datadir}/applications/*.desktop"

RDEPENDS_${PN}-bin = "gstreamer1.0-plugins-base-playback"
RRECOMMENDS_${PN}-bin = "gstreamer1.0-plugins-base-meta \
                         gstreamer1.0-plugins-good-meta \
                         gstreamer1.0-plugins-bad-meta \
                         ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-libav", "", d)} \
                         ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-plugins-ugly-meta", "", d)}"
