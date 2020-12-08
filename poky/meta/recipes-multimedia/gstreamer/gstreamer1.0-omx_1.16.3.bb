SUMMARY = "OpenMAX IL plugins for GStreamer"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=21;md5=5c8e1fca32704488e76d2ba9ddfa935f"

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-omx/gst-omx-${PV}.tar.xz"

SRC_URI[md5sum] = "d4d89dd44362c1d262186c60437cdbee"
SRC_URI[sha256sum] = "60603b7889528ef8539d36cb3284b648c46aa0cf980a28cba4d3fe3a44988ff9"

S = "${WORKDIR}/gst-omx-${PV}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad virtual/libomxil"

inherit meson pkgconfig upstream-version-is-even

GSTREAMER_1_0_OMX_TARGET ?= "bellagio"
GSTREAMER_1_0_OMX_CORE_NAME ?= "${libdir}/libomxil-bellagio.so.0"

EXTRA_OEMESON += "-Dtarget=${GSTREAMER_1_0_OMX_TARGET}"

python __anonymous () {
    omx_target = d.getVar("GSTREAMER_1_0_OMX_TARGET")
    if omx_target in ['generic', 'bellagio']:
        # Bellagio headers are incomplete (they are missing the OMX_VERSION_MAJOR,#
        # OMX_VERSION_MINOR, OMX_VERSION_REVISION, and OMX_VERSION_STEP macros);
        # appending a directory path to gst-omx' internal OpenMAX IL headers fixes this
        d.appendVar("CFLAGS", " -I${S}/omx/openmax")
    elif omx_target == "rpi":
        # Dedicated Raspberry Pi OpenMAX IL support makes this package machine specific
        d.setVar("PACKAGE_ARCH", d.getVar("MACHINE_ARCH"))
}

set_omx_core_name() {
	sed -i -e "s;^core-name=.*;core-name=${GSTREAMER_1_0_OMX_CORE_NAME};" "${D}${sysconfdir}/xdg/gstomx.conf"
}
do_install[postfuncs] += " set_omx_core_name "

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"

VIRTUAL-RUNTIME_libomxil ?= "libomxil"
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_libomxil}"
