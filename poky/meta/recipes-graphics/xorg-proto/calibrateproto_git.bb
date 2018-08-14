require xorg-proto-common.inc

SUMMARY = "XCalibrate: Touchscreen calibration headers"

DESCRIPTION = "This package provides the wire protocol for the \
Touchscreen calibration extension."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://xcalibratewire.h;endline=23;md5=7f86ef7b03cce6c4c9ebd59d20ca485f \
                    file://xcalibrateproto.h;endline=23;md5=e4490491edcc171ca24f98569ee580db"

SRCREV = "1da6fd1e2c7a49648245c98481fabea8b9690a8c"

PV = "0.0+git${SRCPV}"
PR = "r2"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/proto/calibrateproto \
           file://fix.patch;apply=yes"
S = "${WORKDIR}/git"
UPSTREAM_CHECK_COMMITS = "1"

