SUMMARY = "OpenBMC checkstop monitor."
DESCRIPTION = "The checkstop monitor watches a GPIO for a checkstop signal \
and reboots a server."
PR = "r1"

inherit skeleton-gdbus

SKELETON_DIR = "hostcheckstop"
