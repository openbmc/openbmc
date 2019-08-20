require xorg-proto-common.inc

SUMMARY = "XCalibrate: Touchscreen calibration headers"

DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=b9e051107d5628966739a0b2e9b32676"

SRC_URI[md5sum] = "802ccb9e977ba3cf94ba798ddb2898a4"
SRC_URI[sha256sum] = "a6daaa7a6cbc8e374032d83ff7f47d41be98f1e0f4475d66a4da3aa766a0d49b"

BBCLASSEXTEND = "native nativesdk"
