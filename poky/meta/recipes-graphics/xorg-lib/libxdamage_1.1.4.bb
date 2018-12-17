SUMMARY = "Xdamage: X Damage extension library"

DESCRIPTION = "'Damage' is a term that describes changes make to pixel \
contents of windows and pixmaps.  Damage accumulates as drawing occurs \
in the drawable.  Each drawing operation 'damages' one or more \
rectangular areas within the drawable.  The rectangles are guaranteed to \
include the set of pixels modified by each operation, but may include \
significantly more than just those pixels.  The DAMAGE extension allows \
applications to either receive the raw rectangles as a stream of events, \
or to have them partially processed within the X server to reduce the \
amount of data transmitted as well as reduce the processing latency once \
the repaint operation has started."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=9fe101f30dd24134cf43146863241868"

DEPENDS += "virtual/libx11 xorgproto libxfixes"
PROVIDES = "xdamage"
BBCLASSEXTEND = "native"

PE = "1"

XORG_PN = "libXdamage"

SRC_URI[md5sum] = "0cf292de2a9fa2e9a939aefde68fd34f"
SRC_URI[sha256sum] = "7c3fe7c657e83547f4822bfde30a90d84524efb56365448768409b77f05355ad"
