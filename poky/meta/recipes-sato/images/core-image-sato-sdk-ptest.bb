require core-image-sato-sdk.bb

DESCRIPTION += "Also includes ptest packages."

IMAGE_FEATURES += "ptest-pkgs"

# This image is sufficiently large (~1.8GB) that we need to be careful that it fits in a live
# image (which has a 4GB limit), so nullify the overhead factor (1.3x out of the
# box) and explicitly add just 1200MB.
# strace-ptest in particular needs more than 500MB
IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "1224288"

# ptests need more memory than standard to avoid the OOM killer
QB_MEM = "-m 1024"
