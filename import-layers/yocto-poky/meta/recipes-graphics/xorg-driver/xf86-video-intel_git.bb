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
           file://disable-x11-dri3.patch \
           file://always_include_xorg_server.h.patch \
           "

SRC_URI[md5sum] = "fa196a66e52c0c624fe5d350af7a5e7b"
SRC_URI[sha256sum] = "00b781eea055582820a123c47b62411bdf6aabf4f03dc0568faec55faf9667c9"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

DEPENDS += "virtual/libx11 drm libpciaccess pixman"

PACKAGECONFIG ??= "xvmc uxa udev ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri dri1 dri2', '', d)}"

PACKAGECONFIG[dri] = "--enable-dri,--disable-dri"
PACKAGECONFIG[dri1] = "--enable-dri1,--disable-dri1,xf86driproto"
PACKAGECONFIG[dri2] = "--enable-dri2,--disable-dri2,dri2proto"
PACKAGECONFIG[dri3] = "--enable-dri3,--disable-dri3,dri3proto"
PACKAGECONFIG[sna] = "--enable-sna,--disable-sna"
PACKAGECONFIG[uxa] = "--enable-uxa,--disable-uxa"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc xcb-util"
PACKAGECONFIG[tools] = "--enable-tools,--disable-tools,libxinerama libxrandr libxdamage libxfixes libxcursor libxtst libxext libxrender"

# --enable-kms-only option is required by ROOTLESS_X
EXTRA_OECONF += '${@base_conditional( "ROOTLESS_X", "1", " --enable-kms-only", "", d )}'

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

FILES_${PN} += "${datadir}/polkit-1"
