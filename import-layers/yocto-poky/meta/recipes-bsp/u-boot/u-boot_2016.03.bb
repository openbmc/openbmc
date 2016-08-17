require u-boot.inc

DEPENDS += "dtc-native"

# This revision corresponds to the tag "v2016.03"
# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "df61a74e6845ec9bdcdd48d2aff5e9c2c6debeaa"

PV = "v2016.03+git${SRCPV}"
