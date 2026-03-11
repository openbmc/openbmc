SUMMARY = "Xcomposite: X Composite extension library"

DESCRIPTION = "The composite extension provides three related \
mechanisms: per-hierarchy storage, automatic shadow update, and external \
parent.  In per-hierarchy storage, the rendering of an entire hierarchy \
of windows is redirected to off-screen storage.  In automatic shadow \
update, when a hierarchy is rendered off-screen, the X server provides \
an automatic mechanism for presenting those contents within the parent \
window.  In external parent, a mechanism for providing redirection of \
compositing transformations through a client."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f2907aad541f6f226fbc58cc1b3cdf1"

DEPENDS += " xorgproto virtual/libx11 libxfixes libxext"
PROVIDES = "xcomposite"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

XORG_PN = "libXcomposite"

SRC_URI[sha256sum] = "fe40bcf0ae1a09070eba24088a5eb9810efe57453779ec1e20a55080c6dc2c87"
