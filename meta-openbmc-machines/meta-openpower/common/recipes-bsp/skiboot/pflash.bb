SUMMARY = "pflash programmer for OpenPower"
DESCRIPTION = "pflash firmware programming tool for OpenPower machines"

require skiboot.inc
EXTRA_OEMAKE_append = " PFLASH_VERSION=${PV} LINKAGE=dynamic"
