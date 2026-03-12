require recipes-security/optee/optee-client.inc

# v4.9.0
SRCREV = "9f5e90918093c1d1cd264d8149081b64ab7ba672"
PV .= "+git"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
