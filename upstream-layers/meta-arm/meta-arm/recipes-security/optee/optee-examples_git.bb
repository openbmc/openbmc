require recipes-security/optee/optee-examples.inc

# v4.10.0
SRCREV = "934c7edb74a26e90f68024cf441073528444177f"
PV .= "+git"
UPSTREAM_CHECK_COMMITS = "1"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
