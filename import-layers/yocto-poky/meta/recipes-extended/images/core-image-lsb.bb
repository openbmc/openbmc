DESCRIPTION = "An image containing packages that are required to conform \
to the Linux Standard Base (LSB) specification."

IMAGE_FEATURES += "splash ssh-server-openssh hwcodecs package-management"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    packagegroup-core-full-cmdline \
    packagegroup-core-lsb \
    "

inherit core-image distro_features_check

REQUIRED_DISTRO_FEATURES = "pam"
