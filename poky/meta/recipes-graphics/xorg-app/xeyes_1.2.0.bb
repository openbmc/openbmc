require xorg-app-common.inc

SUMMARY = "X11 eyes that follow the mouse cursor demo"
DESCRIPTION = "Xeyes is a small X11 application that shows a pair of eyes that move to \
follow the location of the mouse cursor around the screen."

PE = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6"

SRC_URI[sha256sum] = "f8a17e23146bef1ab345a1e303c6749e42aaa7bcf4f25428afad41770721b6db"

DEPENDS += "libxau libxt libxext libxmu libxrender libxi"
