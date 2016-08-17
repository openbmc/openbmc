SUMMARY = "Take photos and videos with your webcam, with fun graphical effects"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a17cb0a873d252440acfdf9b3d0e7fbf"

DEPENDS = "gtk+ gstreamer gst-plugins-base libcanberra udev librsvg gnome-desktop evolution-data-server intltool-native"

PR = "r2"

inherit gnome

SRC_URI[archive.md5sum] = "1599fded8a1797ea51fb010af4e6c45b"
SRC_URI[archive.sha256sum] = "48f03470c6f527caa0e3b269d3afcff86ae0939a74f66ce030d4eed3bc3cbd9a"
GNOME_COMPRESS_TYPE="bz2"

FILES_${PN} += "${datadir}/dbus-1"
RRECOMMENDS_${PN} = "gst-plugins-good-meta gst-plugins-base-meta"

EXTRA_OECONF += "--disable-scrollkeeper"

do_configure_prepend() {
    sed -i -e "s: help : :g" ${S}/Makefile.am
}
