SUMMARY = "OpenBMC org.openbmc.Flash example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Flash DBUS API. \
org.openbmc.Flash provides APIs for functions like BIOS flash access control \
and updating."
PR = "r1"

inherit skeleton-gdbus

SKELETON_DIR = "flashbios"
