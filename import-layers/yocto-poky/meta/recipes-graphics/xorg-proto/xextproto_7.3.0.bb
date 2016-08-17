require xorg-proto-common.inc

SUMMARY = "XExt: X Extension headers"

DESCRIPTION = "This package provides the wire protocol for several X \
extensions.  These protocol extensions include DOUBLE-BUFFER, DPMS, \
Extended-Visual-Information, LBX, MIT_SHM, MIT_SUNDRY-NONSTANDARD, \
Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, XC-APPGROUP, XC-MISC, \
XTEST.  In addition a small set of utility functions are also \
available."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=86f273291759d0ba2a22585cd1c06c53"

PE = "1"

inherit gettext

EXTRA_OECONF_append = " --enable-specs=no"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "70c90f313b4b0851758ef77b95019584"
SRC_URI[sha256sum] = "f3f4b23ac8db9c3a9e0d8edb591713f3d70ef9c3b175970dd8823dfc92aa5bb0"
