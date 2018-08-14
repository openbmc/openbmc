require core-image-sato-sdk.bb

DESCRIPTION += "Also includes ptest packages."

IMAGE_FEATURES += "ptest-pkgs"

# This image is sufficiently large (~3GB) that it can't actually fit in a live
# image (which has a 4GB limit), so nullify the overhead factor (1.3x out of the
# box) and explicitly add just 500MB.
IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "524288"
