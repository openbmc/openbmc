require recipes-security/optee/optee-examples.inc

# v4.11.0
SRCREV = "71e8f3f320eef46a9369700bf30b91d67427d535"
PV .= "+git"
UPSTREAM_CHECK_COMMITS = "1"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
