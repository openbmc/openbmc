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
LIC_FILES_CHKSUM = "file://COPYING;md5=51f7b67ceb9dc21dd53952d0b7727b4e"

DEPENDS += " xorgproto virtual/libx11 libxfixes libxext"
PROVIDES = "xcomposite"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

XORG_PN = "libXcomposite"

SRC_URI[sha256sum] = "8bdf310967f484503fa51714cf97bff0723d9b673e0eecbf92b3f97c060c8ccb"
