require recipes-security/optee/optee-examples.inc

# v4.8.0
SRCREV = "3ef17eb1f309def91113637f95f67613b1d89119"
PV .= "+git"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
