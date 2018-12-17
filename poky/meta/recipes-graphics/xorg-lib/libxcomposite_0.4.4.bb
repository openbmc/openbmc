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
BBCLASSEXTEND = "native"

PE = "1"

XORG_PN = "libXcomposite"

SRC_URI += " file://change-include-order.patch"

SRC_URI[md5sum] = "f7a218dcbf6f0848599c6c36fc65c51a"
SRC_URI[sha256sum] = "ede250cd207d8bee4a338265c3007d7a68d5aca791b6ac41af18e9a2aeb34178"
