require xorg-driver-video.inc

SUMMARY = "X.Org X server -- Texas Instruments OMAP framebuffer driver"

DESCRIPTION = "omapfb driver supports the basic Texas Instruments OMAP \
framebuffer."

LICENSE = "MIT-X & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=63e2cbac53863f60e2f43343fb34367f"
DEPENDS += "virtual/libx11"

SRCREV = "33e36c12dde336edbdd34626dd8adfcaebc8fbb8"
PR = "${INC_PR}.7"
PV = "0.1.1+gitr${SRCPV}"

# Blacklist debian-specific tags in upstream version check
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)(?!-)"

SRC_URI = "git://anonscm.debian.org/collab-maint/xf86-video-omapfb.git \
  file://0001-Prevents-omapfb-from-from-crashing-when-pixelclock-o.patch \
  file://0001-Revert-Set-a-large-CRTC-upper-limit-to-not-prune-lar.patch \
  file://0002-Revert-Set-virtual-size-when-configuring-framebuffer.patch \
  file://0003-force-plain-mode.patch \
  file://0004-blacklist-tv-out.patch \
  file://0005-Attempt-to-fix-VRFB.patch \
  file://0006-omapfb-port-to-new-xserver-video-API.patch \
  file://0007-always_include_xorg_server.h.patch \
"

S = "${WORKDIR}/git"

CFLAGS += " -I${STAGING_INCDIR}/xorg "

# Use overlay 2 on omap3 to enable other apps to use overlay 1 (e.g. dmai or omapfbplay)
do_compile_prepend_armv7a () {
        sed -i -e s:fb1:fb2:g ${S}/src/omapfb-xv.c
}
