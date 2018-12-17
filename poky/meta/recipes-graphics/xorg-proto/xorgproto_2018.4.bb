require xorg-proto-common.inc

SUMMARY = "XCalibrate: Touchscreen calibration headers"

DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=b9e051107d5628966739a0b2e9b32676"

SRC_URI += "file://0001-Remove-libdir-specification.patch"

SRC_URI[md5sum] = "81557ca47ee66a4e54590fcdadd28114"
SRC_URI[sha256sum] = "fee885e0512899ea5280c593fdb2735beb1693ad170c22ebcc844470eec415a0"

BBCLASSEXTEND = "native nativesdk"
