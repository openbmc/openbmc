SUMMARY = "OpenPOWER flashing utility."
DESCRIPTION = "A BMC/BIOS flashing utility for use on OpenPOWER system."
DEPENDS += "pflash"
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "op-flasher"

inherit skeleton-gdbus
inherit pkgconfig

RDEPENDS:${PN} += "pflash"
