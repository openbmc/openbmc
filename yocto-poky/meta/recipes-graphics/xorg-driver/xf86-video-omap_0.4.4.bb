require xorg-driver-video.inc

SUMMARY = "X.Org X server -- Texas Instruments OMAP framebuffer driver"

SUMMARY = "X.Org X server -- TI OMAP integrated graphics chipsets driver"

DESCRIPTION = "Open-source X.org graphics driver for TI OMAP graphics \
Currently relies on a closed-source submodule for EXA acceleration on \
the following chipsets: \
  + OMAP3430 \
  + OMAP3630 \
  + OMAP4430 \
  + OMAP4460 \
  + OMAP5430 \
  + OMAP5432 \
\
NOTE: this driver is work in progress..  you probably don't want to try \
and use it yet.  The API/ABI between driver and kernel, and driver and \
acceleration submodules is not stable yet.  This driver requires the \
omapdrm kernel driver w/ GEM support. \
"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=10ce5de3b111315ea652a5f74ec0c602"
DEPENDS += "virtual/libx11 libdrm xf86driproto"

SRC_URI[md5sum] = "a364e151a537948f75bb8c9b840a31da"
SRC_URI[sha256sum] = "a7192690b8638f5c8d611a1bae07cc13081e22edd4070f37a2f5c3f8d79e7ebc"

CFLAGS += " -I${STAGING_INCDIR}/xorg "

# Use overlay 2 on omap3 to enable other apps to use overlay 1 (e.g. dmai or omapfbplay)
do_compile_prepend_armv7a () {
        sed -i -e s:fb1:fb2:g ${S}/src/omap_xv.c
}

python () {
    if not bb.utils.contains ('DISTRO_FEATURES', 'opengl', True, False, d):
        raise bb.parse.SkipPackage("'opengl' not in DISTRO_FEATURES")
}

