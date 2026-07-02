require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "analog / digital clock for X"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2356ccad6224ad00334a72040730eb4b"

DEPENDS += " libxaw libxrender libxft libxkbfile libxt"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "410f0372f8381efd9a8282d8227122bab882d7cffe2f7f8f886dd9876d5fc875"
