require xorg-driver-video.inc

SUMMARY = "X.Org X server -- Intel integrated graphics chipsets driver"

DESCRIPTION = "intel is an Xorg driver for Intel integrated graphics \
chipsets. The driver supports depths 8, 15, 16 and 24. On some chipsets, \
the driver supports hardware accelerated 3D via the Direct Rendering \
Infrastructure (DRI)."

LIC_FILES_CHKSUM = "file://COPYING;md5=8730ad58d11c7bbad9a7066d69f7808e"

SRCREV = "e4fe79cf0d9a05ee3f3a027148ef0aeb2b1b34e1"
PV = "2.99.917+git${SRCPV}"
S = "${WORKDIR}/git"

SRC_URI = "git://anongit.freedesktop.org/xorg/driver/xf86-video-intel \
           file://0001-Add-Coffeelake-PCI-IDs-for-S-Skus.patch \
           file://glibc.patch \
           "

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

DEPENDS += "virtual/libx11 drm libpciaccess pixman"

PACKAGECONFIG ??= "xvmc uxa udev ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri dri2 dri3', '', d)}"

PACKAGECONFIG[dri] = "--enable-dri,--disable-dri"
PACKAGECONFIG[dri1] = "--enable-dri1,--disable-dri1"
PACKAGECONFIG[dri2] = "--enable-dri2,--disable-dri2"
PACKAGECONFIG[dri3] = "--enable-dri3,--disable-dri3"
PACKAGECONFIG[sna] = "--enable-sna,--disable-sna"
PACKAGECONFIG[uxa] = "--enable-uxa,--disable-uxa"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc xcb-util"
PACKAGECONFIG[tools] = "--enable-tools,--disable-tools,libxinerama libxrandr libxdamage libxfixes libxcursor libxtst libxrender libxscrnsaver libxext libx11 pixman libxcb libxshmfence"

# --enable-kms-only option is required by ROOTLESS_X
EXTRA_OECONF += '${@oe.utils.conditional( "ROOTLESS_X", "1", " --enable-kms-only", "", d )}'

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

FILES_${PN} += "${datadir}/polkit-1"
