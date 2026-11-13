require recipes-security/optee/optee-client.inc

# v4.11.0
SRCREV = "da81fb7b30781029bc9d4acfddc7c60b08deb1f3"
PV .= "+git"
UPSTREAM_CHECK_COMMITS = "1"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
