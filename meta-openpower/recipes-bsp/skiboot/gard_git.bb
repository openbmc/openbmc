SUMMARY = "gard record tool for OpenPower"
DESCRIPTION = "gard record tool for OpenPower machines"

require skiboot.inc
EXTRA_OEMAKE:append = " PFLASH_VERSION=${PV} GARD_VERSION=${PV}"
