require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm \
            virtual/libgl xorgproto libpciaccess"

inherit features_check
REQUIRED_DISTRO_FEATURES += "opengl"

SRC_URI += "file://0001-Fix-link-failure-with-gcc-10.patch \
            file://0001-ati-cleanup-terminology-to-use-primary-secondary.patch \
           "
SRC_URI[sha256sum] = "659f5a1629eea5f5334d9b39b18e6807a63aa1efa33c1236d9cc53acbb223c49"

#EXTRA_OECONF += "--disable-glamor"

RDEPENDS:${PN} += "xserver-xorg-module-exa"
RRECOMMENDS:${PN} += "linux-firmware-radeon"

FILES:${PN} += "${datadir}/X11"
