require u-boot.inc

DEPENDS += "dtc-native"

# This revision corresponds to the tag "v2015.07"
# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "33711bdd4a4dce942fb5ae85a68899a8357bdd94"

SRC_URI += "file://0001-u-boot-mpc85xx-u-boot-.lds-remove-_GLOBAL_OFFSET_TAB.patch"

PV = "v2015.07+git${SRCPV}"

EXTRA_OEMAKE_append = " KCFLAGS=-fgnu89-inline"
