SUMMARY = "A very basic X11 image with a terminal"

IMAGE_FEATURES += "splash package-management x11-base"

LICENSE = "MIT"

inherit core-image distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"
