require xorg-data-common.inc

SUMMARY = "Common X11 Bitmaps"
LICENSE = "MIT"
DEPENDS += "libxmu"
RDEPENDS_${PN}-dev = ""

LIC_FILES_CHKSUM = "file://COPYING;md5=dbd075aaffa4a60a8d00696f2e4b9a8f"

SRC_URI[md5sum] = "cedeef095918aca86da79a2934e03daf"
SRC_URI[sha256sum] = "b9f0c71563125937776c8f1f25174ae9685314cbd130fb4c2efce811981e07ee"
