require xorg-proto-common.inc

SUMMARY = "Xlib: C Language X interface headers"

DESCRIPTION = "This package provides the basic headers for the X Window \
System."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=b9e051107d5628966739a0b2e9b32676"

PE = "1"

SRC_URI += "file://xproto_fix_for_x32.patch"

EXTRA_OECONF_append = " --enable-specs=no"
BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "16791f7ca8c51a20608af11702e51083"
SRC_URI[sha256sum] = "c6f9747da0bd3a95f86b17fb8dd5e717c8f3ab7f0ece3ba1b247899ec1ef7747"
