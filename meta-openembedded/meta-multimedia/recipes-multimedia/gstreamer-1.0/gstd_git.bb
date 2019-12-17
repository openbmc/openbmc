DESCRIPTION = "Gstreamer Daemon"
SUMMARY = "GStreamer framework for controlling audio and video streaming using TCP connection messages"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=Gstd-1.0"
SECTION = "multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gstreamer1.0-rtsp-server json-glib libdaemon"

SRCBRANCH ?= "master"
SRCREV = "3526d0ffdbccc375db7d5fe33a72c68b134657c2"
SRC_URI = "git://git@github.com/RidgeRun/gstd-1.x.git;protocol=https;branch=${SRCBRANCH} \
           file://0001-gstd-yocto-compatibility.patch \
           file://0001-Look-for-gtk-doc.make-in-builddir.patch \
           "
S = "${WORKDIR}/git"

# Remove the +really when upstream version is > 1.0
PV = "1.0+really0.6.3"

inherit autotools pkgconfig gettext gtk-doc

do_configure_prepend() {
	sed -i -e "s|include \$(top_builddir)/docs/gtk-doc.make||g" ${S}/docs/reference/gstd/Makefile.am
}
