SUMMARY = "Video Decode and Presentation API for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=83af8811a28727a13f04132cc33b7f58"

DEPENDS = "virtual/libx11 libxext xorgproto"

SRCREV = "f57a9904c43ef5d726320c77baa91d0c38361ed4"
SRC_URI = "git://anongit.freedesktop.org/vdpau/libvdpau;branch=master"

S = "${WORKDIR}/git"

inherit features_check meson

REQUIRED_DISTRO_FEATURES = "x11"

do_install_append() {
    rm -f ${D}${libdir}/*/*.la
}

FILES_${PN}-dbg += "${libdir}/vdpau/.debug"
FILES_${PN}-dev += "${libdir}/vdpau/lib*${SOLIBSDEV}"
FILES_${PN} += "${libdir}/vdpau/lib*${SOLIBS}"
