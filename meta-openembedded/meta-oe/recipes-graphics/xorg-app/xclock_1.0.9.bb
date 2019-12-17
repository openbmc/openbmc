require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "analog / digital clock for X"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=857759ade8f2ddde5c7b32ef7356ea36"

DEPENDS += " libxaw libxrender libxft libxkbfile libxt"

SRC_URI[md5sum] = "437522a96f424f68fc64ed34ece9b211"
SRC_URI[sha256sum] = "cf461fb2c6f2ac42c54d8429ee2010fdb9a1442a370adfbfe8a7bfaf33c123bb"
