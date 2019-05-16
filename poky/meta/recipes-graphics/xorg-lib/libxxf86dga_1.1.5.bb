require xorg-lib-common.inc

SUMMARY = "XFree86-DGA: XFree86 Direct Graphics Access extension library"

DESCRIPTION = "libXxf86dga provides the XFree86-DGA extension, which \
allows direct graphics access to a framebuffer-like region, and also \
allows relative mouse reporting, et al.  It is mainly used by games and \
emulators for games."

LIC_FILES_CHKSUM = "file://COPYING;md5=abb99ac125f84f424a4278153988e32f"

DEPENDS += "libxext"

PE = "1"

SRC_URI[md5sum] = "0ddeafc13b33086357cfa96fae41ee8e"
SRC_URI[sha256sum] = "2b98bc5f506c6140d4eddd3990842d30f5dae733b64f198a504f07461bdb7203"

XORG_PN = "libXxf86dga"
