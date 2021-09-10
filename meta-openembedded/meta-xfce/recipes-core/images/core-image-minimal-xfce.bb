DESCRIPTION = "A XFCE minimal demo image."

IMAGE_INSTALL = "packagegroup-core-boot \
    packagegroup-core-x11 \
    packagegroup-xfce-base \
    kernel-modules \
"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

export IMAGE_BASENAME = "core-image-minimal-xfce"

inherit core-image

SYSTEMD_DEFAULT_TARGET = "graphical.target"
