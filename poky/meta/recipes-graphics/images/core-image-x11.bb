SUMMARY = "A very basic X11 image with a terminal"

IMAGE_FEATURES += "splash package-management x11-base"

LICENSE = "MIT"

inherit core-image features_check

REQUIRED_DISTRO_FEATURES = "x11"

QB_MEM = '${@bb.utils.contains("DISTRO_FEATURES", "opengl", "-m 512", "-m 256", d)}'
