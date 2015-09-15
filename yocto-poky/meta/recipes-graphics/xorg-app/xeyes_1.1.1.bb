require xorg-app-common.inc

SUMMARY = "X11 eyes that follow the mouse cursor demo"
DESCRIPTION = "Xeyes is a small X11 application that shows a pair of eyes that move to \
follow the location of the mouse cursor around the screen."

PE = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6"

SRC_URI[md5sum] = "a3035dcecdbdb89e864177c080924981"
SRC_URI[sha256sum] = "975e98680cd59e1f9439016386609546ed08c284d0f05a95276f96aca6e8a521"

DEPENDS += "libxau libxt libxext libxmu libxrender"
