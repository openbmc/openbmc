require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

# v4.10.0
SRCREV = "753afbbee1682f5d16fd30e87b31058a4fd4f4b8"
PV .= "+git"
UPSTREAM_CHECK_COMMITS = "1"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
