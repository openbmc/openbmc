SUMMARY = "Video Decode and Presentation API for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=83af8811a28727a13f04132cc33b7f58"

DEPENDS = "virtual/libx11 libxext xorgproto"

SRCREV = "c3d1a9dbafdfe6144ff474d0d523dc01b068750f"
SRC_URI = "git://anongit.freedesktop.org/vdpau/libvdpau;branch=master"

S = "${WORKDIR}/git"

inherit features_check meson pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

do_install:append() {
    rm -f ${D}${libdir}/*/*.la
}

FILES:${PN}-dbg += "${libdir}/vdpau/.debug"
FILES:${PN}-dev += "${libdir}/vdpau/lib*${SOLIBSDEV}"
FILES:${PN} += "${libdir}/vdpau/lib*${SOLIBS}"
