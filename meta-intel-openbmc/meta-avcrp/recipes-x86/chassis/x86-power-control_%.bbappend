FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

EXTRA_OEMESON:append = " -Dconfig-file=intel/power-config-avcrp"