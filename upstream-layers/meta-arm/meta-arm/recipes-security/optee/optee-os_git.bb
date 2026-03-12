require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

# v4.8.0
SRCREV = "86660925433a8d4d1b19cfa5fe940081d77b34b4"
PV .= "+git"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
