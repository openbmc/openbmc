SUMMARY = "pflash programmer for OpenPower"
DESCRIPTION = "pflash firmware programming tool for OpenPower machines"

require skiboot.inc
EXTRA_OEMAKE_append = " PFLASH_VERSION=${PV} LINKAGE=dynamic"

# Fix GNU_HASH warning
INSANE_SKIP_${PN} = "ldflags" 
INSANE_SKIP_${PN}-dev = "ldflags" 
INSANE_SKIP_${PN}-staticdev = "ldflags"
