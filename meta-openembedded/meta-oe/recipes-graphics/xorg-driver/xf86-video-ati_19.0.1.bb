require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm \
            virtual/libgl xorgproto libpciaccess"

inherit features_check
REQUIRED_DISTRO_FEATURES += "opengl"

SRC_URI += "file://0001-Fix-link-failure-with-gcc-10.patch"

SRC_URI[md5sum] = "47eccf71823206ade9629cba69de7ef6"
SRC_URI[sha256sum] = "5cb6015d8664546ad1311bc9c363d7bc41ebf60e7046ceb44dd38e5b707961b0"

EXTRA_OECONF += "--disable-glamor"

RDEPENDS_${PN} += "xserver-xorg-module-exa"
RRECOMMENDS_${PN} += "linux-firmware-radeon"

FILES_${PN} += "${datadir}/X11"
