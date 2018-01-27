SUMMARY = "OpenPOWER flashing utility."
DESCRIPTION = "A BMC/BIOS flashing utility for use on OpenPOWER system."
PR = "r1"

inherit skeleton-gdbus
inherit pkgconfig

DEPENDS += "pflash"
RDEPENDS_${PN} += "pflash"

SKELETON_DIR = "op-flasher"
