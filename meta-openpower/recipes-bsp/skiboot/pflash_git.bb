SUMMARY = "pflash programmer for OpenPower"
DESCRIPTION = "pflash firmware programming tool for OpenPower machines"

require skiboot.inc
EXTRA_OEMAKE_append = " PFLASH_VERSION=${PV} LINKAGE=dynamic"

#TODO: openbmc/openbmc#1361 - Fix GNU_HASH warnings in pflash
TARGET_CC_ARCH += "${LDFLAGS}"
