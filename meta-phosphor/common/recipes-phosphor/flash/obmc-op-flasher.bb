SUMMARY = "OpenPOWER flashing utility."
DESCRIPTION = "A BMC/BIOS flashing utility for use on OpenPOWER system."
PR = "r1"

inherit skeleton-gdbus

# DEPEND,RDEPEND on pflash if the openpower-pflash machine feature is set.
PACKAGECONFIG ??= "${@bb.utils.contains('MACHINE_FEATURES', 'openpower-pflash', 'openpower-pflash', '', d)}"
PACKAGECONFIG[openpower-pflash] = ",,pflash,pflash"

SKELETON_DIR = "op-flasher"
