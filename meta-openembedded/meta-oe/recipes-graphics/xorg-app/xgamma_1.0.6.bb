require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "xgamma allows X users to query and alter the gamma \
correction of a monitor via the X video mode extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=ac9801b8423fd7a7699ccbd45cf134d8"

DEPENDS += "libxxf86vm"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "90b4305157c2b966d5180e2ee61262be"
SRC_URI[sha256sum] = "0ef1c35b5c18b1b22317f455c8df13c0a471a8efad63c89c98ae3ce8c2b222d3"
