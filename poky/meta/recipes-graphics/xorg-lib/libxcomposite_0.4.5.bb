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

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f2907aad541f6f226fbc58cc1b3cdf1"

DEPENDS += " xorgproto virtual/libx11 libxfixes libxext"
PROVIDES = "xcomposite"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

XORG_PN = "libXcomposite"

SRC_URI += " file://change-include-order.patch"

SRC_URI[md5sum] = "3fa0841ea89024719b20cd702a9b54e0"
SRC_URI[sha256sum] = "b3218a2c15bab8035d16810df5b8251ffc7132ff3aa70651a1fba0bfe9634e8f"
