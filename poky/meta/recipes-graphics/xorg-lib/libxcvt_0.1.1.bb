SUMMARY = "Library providing a standalone version of the X server \
implementation of the VESA CVT standard timing modelines generator"
HOMEPAGE = "https://gitlab.freedesktop.org/xorg/lib/libxcvt"
BUGTRACKER = "https://gitlab.freedesktop.org/xorg/lib/libxcvt/-/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=129947a06984d6faa6f9a9788fa2a03f"
SECTION = "x11/libs"

SRC_URI = "git://gitlab.freedesktop.org/xorg/lib/libxcvt.git;protocol=https;branch=master"
SRCREV = "6fe840b9295cfdc41bd734586c5b8756f6af6f9b"

S = "${WORKDIR}/git"

inherit meson

FILES:${PN} = " \
    ${libdir}/libxcvt.so.0* \
    ${bindir}/cvt \
"
