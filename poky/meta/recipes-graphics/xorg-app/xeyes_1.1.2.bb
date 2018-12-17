require xorg-app-common.inc

SUMMARY = "X11 eyes that follow the mouse cursor demo"
DESCRIPTION = "Xeyes is a small X11 application that shows a pair of eyes that move to \
follow the location of the mouse cursor around the screen."

PE = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6"

SRC_URI[md5sum] = "6f0543ec84283df5743eeafc173bea4a"
SRC_URI[sha256sum] = "57bcec0d2d167af9e5d44d0dbd74c6d7c0f0591cd0608952b23c749fdd910553"

DEPENDS += "libxau libxt libxext libxmu libxrender"
