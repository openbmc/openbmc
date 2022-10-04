require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm \
            virtual/libgl xorgproto libpciaccess"

inherit features_check

REQUIRED_DISTRO_FEATURES += "opengl"

SRC_URI = "git://git@gitlab.freedesktop.org/xorg/driver/xf86-video-ati.git;branch=master"
SRCREV = "7a6a34af026f0bef8080b91baf97a729380bca01"

SRC_URI[sha256sum] = "659f5a1629eea5f5334d9b39b18e6807a63aa1efa33c1236d9cc53acbb223c49"

PV = "19.1.0+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "udev"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor"

RDEPENDS:${PN}:append = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'glamor', 'libegl', '', d)} \
    mesa-megadriver \
    xserver-xorg-extension-glx \
    xserver-xorg-module-exa \
"

RRECOMMENDS:${PN} += "linux-firmware-radeon"

PACKAGES =+ "${PN}-config"

FILES:${PN}-config = "${datadir}/X11/xorg.conf.d"
FILES:${PN} += "${datadir}/X11"
