SUMMARY = "OpenBMC org.openbmc.control.Bmc example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.control.Bmc DBUS API. \
org.openbmc.control.Bmc provides APIs for functions like resetting the BMC."
PR = "r1"

inherit skeleton-gdbus

SKELETON_DIR = "bmcctl"
