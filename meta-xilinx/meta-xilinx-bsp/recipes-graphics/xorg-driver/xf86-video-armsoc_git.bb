require recipes-graphics/xorg-driver/xorg-driver-video.inc

SUMMARY = "X.Org X server -- Xilinx ARM SOC display driver"
DESCRIPTION = "Xilinx ARM SOC display driver "

LICENSE = "MIT-X & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=10ce5de3b111315ea652a5f74ec0c602"

DEPENDS += "virtual/libx11 libdrm xf86driproto"
RDEPENDS_${PN} += "xserver-xorg-module-exa"

PV = "1.4.1+git${SRCPV}"

SRCREV = "8bbdb2ae3bb8ef649999a8da33ddbe11a04763b8"
SRC_URI = " \
	git://anongit.freedesktop.org/xorg/driver/xf86-video-armsoc \
	file://0001-src-drmmode_xilinx-Add-the-dumb-gem-support-for-Xili.patch \
	"

S = "${WORKDIR}/git"

EXTRA_OECONF = " --enable-maintainer-mode"
CFLAGS += " -I${STAGING_INCDIR}/xorg "
