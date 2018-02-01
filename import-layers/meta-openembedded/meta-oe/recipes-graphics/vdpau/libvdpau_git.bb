SUMMARY = "Video Decode and Presentation API for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=83af8811a28727a13f04132cc33b7f58"

DEPENDS = "virtual/libx11 libxext dri2proto"

PV = "1.1.1+git${SRCPV}"

SRCREV = "a21bf7aa438f5dd40d0a300a3167aa3d6f26dccc"
SRC_URI = "git://anongit.freedesktop.org/vdpau/libvdpau"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

do_install_append() {
    rm -f ${D}${libdir}/*/*.la
}

FILES_${PN}-dbg += "${libdir}/vdpau/.debug"
FILES_${PN}-dev += "${libdir}/vdpau/lib*${SOLIBSDEV}"
FILES_${PN} += "${libdir}/vdpau/lib*${SOLIBS}"
