# TODO: openbmc/openbmc#1407 - Remove with RHEL6 support deprecation.
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI += "file://0001-Remove-Wstack-usage-to-support-old-gcc.patch"
