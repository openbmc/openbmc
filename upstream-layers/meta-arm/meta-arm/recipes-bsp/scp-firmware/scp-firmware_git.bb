require recipes-bsp/scp-firmware/scp-firmware.inc

SRCBRANCH = "main"
SRCREV  = "57bb4a303d848f11cef182b24ef16060d371858c"

# For now we only build with GCC, so stop meta-clang trying to get involved
TOOLCHAIN = "gcc"

# Not a release recipe, try our hardest to not pull this in implicitly
DEFAULT_PREFERENCE = "-1"
UPSTREAM_CHECK_COMMITS = "1"
