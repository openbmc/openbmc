require xorg-proto-common.inc

SUMMARY = "XC-MISC: X XC-Miscellaneous extension headers"

DESCRIPTION = "This package provides the wire protocol for the XC-MISC \
extension, which is used to get details of XID allocations within the \
server."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=09d83047c15994e05db29b423ed6662e"

PE = "1"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "5f4847c78e41b801982c8a5e06365b24"
SRC_URI[sha256sum] = "b13236869372256c36db79ae39d54214172677fb79e9cdc555dceec80bd9d2df"
