# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Gstreamer editing services"
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-editing-services/"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d"

DEPENDS = "flex-native gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad python3-pygobject"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

inherit meson pkgconfig upstream-version-is-even gobject-introspection features_check bash-completion

EXTRA_OEMESON = "-Dvalidate=disabled"

SRC_URI = "http://gstreamer.freedesktop.org/src/gst-editing-services/gst-editing-services-${PV}.tar.xz"
SRC_URI[sha256sum] = "363f2b13675877b926b4be5259dbbeea8cc976805b40c871fc254bb8f382017d"

PACKAGES += "gst-validate-launcher libges"

FILES:gst-validate-launcher = "${nonarch_libdir}/gst-validate-launcher ${datadir}/gstreamer-1.0/validate"
FILES:libges = "${libdir}/gstreamer-1.0/*.so"
