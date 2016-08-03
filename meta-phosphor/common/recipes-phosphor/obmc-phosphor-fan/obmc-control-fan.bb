SUMMARY = "OpenBMC fan control"
DESCRIPTION = "OpenBMC fan control."
PR = "r1"

inherit skeleton-sdbus

RDEPENDS_${PN} += "libsystemd"
SKELETON_DIR = "fanctl"
