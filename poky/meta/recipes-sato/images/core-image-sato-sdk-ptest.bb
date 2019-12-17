require core-image-sato-sdk.bb
require conf/distro/include/ptest-packagelists.inc

DESCRIPTION += "Also includes ptest packages."

IMAGE_FEATURES += "ptest-pkgs"

PROVIDES += "core-image-sato-ptest"

# Also include ptests which may not otherwise be included in a sato image
IMAGE_INSTALL += "${PTESTS_FAST} ${PTESTS_SLOW}"

# This image is sufficiently large (~1.8GB) that we need to be careful that it fits in a live
# image (which has a 4GB limit), so nullify the overhead factor (1.3x out of the
# box) and explicitly add just 1100MB.
# strace-ptest in particular needs more than 500MB
IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "1124288"

# ptests need more memory than standard to avoid the OOM killer
QB_MEM = "-m 1024"
