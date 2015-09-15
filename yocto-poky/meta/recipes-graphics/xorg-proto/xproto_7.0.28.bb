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

SRC_URI[md5sum] = "3ce2f230c5d8fa929f326ad1f0fa40a8"
SRC_URI[sha256sum] = "29e85568d1f68ceef8a2c081dad9bc0e5500a53cfffde24b564dc43d46ddf6ca"

