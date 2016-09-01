SUMMARY = "org.openbmc.control.Power implemention for OpenPOWER"
DESCRIPTION = "A power control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus

DEPENDS += "obmc-mapper systemd"

SKELETON_DIR = "op-pwrctl"
