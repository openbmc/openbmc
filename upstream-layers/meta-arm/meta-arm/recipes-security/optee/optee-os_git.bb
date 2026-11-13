require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

# v4.11.0
SRCREV = "f54190f22611dd5ad5d22546fd4209aa100f56bd"
PV .= "+git"
UPSTREAM_CHECK_COMMITS = "1"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
