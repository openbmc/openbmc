require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xsetroot/"
DESCRIPTION = "xsetroot is a root window parameter setting utility for X"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ea29dbee22324787c061f039e0529de"

DEPENDS += "xbitmaps libxcursor"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "7211b31ec70631829ebae9460999aa0b"
SRC_URI[sha256sum] = "ba215daaa78c415fce11b9e58c365d03bb602eaa5ea916578d76861a468cc3d9"
