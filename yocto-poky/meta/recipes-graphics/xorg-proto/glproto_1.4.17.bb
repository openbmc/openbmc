require xorg-proto-common.inc

SUMMARY = "OpenGL: X OpenGL extension headers"

DESCRIPTION = "This package provides the wire protocol for the \
OpenGL-related extensions, used to enable the rendering of applications \
using OpenGL."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d44ed0146997856304dfbb512a59a8de \
                    file://glxproto.h;beginline=4;endline=32;md5=6b79c570f644363b356456e7d44471d9"

PE = "1"

BBCLASSEXTEND = "nativesdk"

SRC_URI[md5sum] = "5565f1b0facf4a59c2778229c1f70d10"
SRC_URI[sha256sum] = "adaa94bded310a2bfcbb9deb4d751d965fcfe6fb3a2f6d242e2df2d6589dbe40"
