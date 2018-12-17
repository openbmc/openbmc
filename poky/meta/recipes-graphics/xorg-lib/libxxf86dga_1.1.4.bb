require xorg-lib-common.inc

SUMMARY = "XFree86-DGA: XFree86 Direct Graphics Access extension library"

DESCRIPTION = "libXxf86dga provides the XFree86-DGA extension, which \
allows direct graphics access to a framebuffer-like region, and also \
allows relative mouse reporting, et al.  It is mainly used by games and \
emulators for games."

LIC_FILES_CHKSUM = "file://COPYING;md5=abb99ac125f84f424a4278153988e32f"

DEPENDS += "libxext"

PE = "1"

SRC_URI[md5sum] = "d7dd9b9df336b7dd4028b6b56542ff2c"
SRC_URI[sha256sum] = "8eecd4b6c1df9a3704c04733c2f4fa93ef469b55028af5510b25818e2456c77e"

XORG_PN = "libXxf86dga"
