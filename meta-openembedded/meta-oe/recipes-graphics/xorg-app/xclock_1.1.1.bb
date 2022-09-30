require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "analog / digital clock for X"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=857759ade8f2ddde5c7b32ef7356ea36"

DEPENDS += " libxaw libxrender libxft libxkbfile libxt"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "df7ceabf8f07044a2fde4924d794554996811640a45de40cb12c2cf1f90f742c"
