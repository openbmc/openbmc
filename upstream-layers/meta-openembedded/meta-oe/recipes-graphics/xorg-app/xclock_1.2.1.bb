require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "analog / digital clock for X"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2356ccad6224ad00334a72040730eb4b"

DEPENDS += " libxaw libxrender libxft libxkbfile libxt"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "e326cac20f7ad2af5412b9c4b70b1c3d392e08cd3cc6246e0a77b7c3935a4851"
