SUMMARY = "A volume control application based on GStreamer"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce-panel-plugin

DEPENDS += "glib-2.0 gst-plugins-base gtk+ xfconf libunique"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "1b3753b91224867a3a2dfddda239c28d"
SRC_URI[sha256sum] = "fb0c1df201ed1130f54f15b914cbe5a59286e994a137acda5609570c57112de2"

RDEPENDS_${PN} = "gst-meta-audio"
