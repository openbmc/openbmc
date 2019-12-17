require xorg-proto-common.inc

SUMMARY = "X Window System unified protocol definitions"

DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=b9e051107d5628966739a0b2e9b32676"

SRC_URI[md5sum] = "a02dcaff48b4141b949ac99dfc344d86"
SRC_URI[sha256sum] = "46ecd0156c561d41e8aa87ce79340910cdf38373b759e737fcbba5df508e7b8e"

BBCLASSEXTEND = "native nativesdk"
