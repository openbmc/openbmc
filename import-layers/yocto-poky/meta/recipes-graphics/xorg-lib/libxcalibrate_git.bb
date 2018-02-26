SUMMARY = "XCalibrate: Touchscreen calibration library"

DESCRIPTION = "libXCalibrate is a library for performing touchscreen \
calibration with the kdrive tslib touchscreen driver."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://xcalibrate.h;endline=21;md5=fa572df6439f0f235f2612f370f153d7 \
                    file://xcalibrate.c;endline=21;md5=fa572df6439f0f235f2612f370f153d7"

DEPENDS = "virtual/libx11 calibrateproto libxext"

SRCREV = "209d83af61ed38a002c8096377deac292b3e396c"
PV = "0.0+git${SRCPV}"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/lib/libXCalibrate \
           file://fix-xcb.patch"
UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/git"

FILES_${PN}-locale += "${datadir}/X11/locale"
