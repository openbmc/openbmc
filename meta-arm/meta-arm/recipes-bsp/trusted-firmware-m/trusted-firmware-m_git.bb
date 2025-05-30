require recipes-bsp/trusted-firmware-m/trusted-firmware-m-git-src.inc
require recipes-bsp/trusted-firmware-m/trusted-firmware-m.inc

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
UPSTREAM_CHECK_COMMITS = "1"
