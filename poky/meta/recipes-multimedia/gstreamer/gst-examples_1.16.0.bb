SUMMARY = "GStreamer examples (including gtk-play, gst-play)"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://playback/player/gtk/gtk-play.c;beginline=1;endline=20;md5=f8c72dae3d36823ec716a9ebcae593b9"

DEPENDS = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gtk+3 glib-2.0-native"

SRC_URI = "git://gitlab.freedesktop.org/gstreamer/gst-examples.git;protocol=https \
           file://0001-Make-player-examples-installable.patch \
           file://gst-player.desktop \
           "

SRCREV = "d953c127c1146b50d5676618299933950685dcd7"

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check


ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

do_install_append() {
	install -m 0644 -D ${WORKDIR}/gst-player.desktop ${D}${datadir}/applications/gst-player.desktop
}

RDEPENDS_${PN} = "gstreamer1.0-plugins-base-playback"
RRECOMMENDS_${PN} = "gstreamer1.0-plugins-base-meta \
                     gstreamer1.0-plugins-good-meta \
                     gstreamer1.0-plugins-bad-meta \
                      ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-libav", "", d)} \
                     ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "gstreamer1.0-plugins-ugly-meta", "", d)}"
RPROVIDES_${PN} += "gst-player gst-player-bin"
