require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"

DESCRIPTION = "xgamma allows X users to query and alter the gamma \
correction of a monitor via the X video mode extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=ac9801b8423fd7a7699ccbd45cf134d8"

DEPENDS += "libxxf86vm"

BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "98f9f69e53a11c354a6637ea5c3d7699ceb5c5b1f8ad6f0a14d9931e5a10d079"
