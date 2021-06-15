DESCRIPTION = "An image with support for the Open GL-based toolkit Clutter, \
which enables development of rich and animated graphical user interfaces."

IMAGE_FEATURES += "splash package-management x11-base x11-sato ssh-server-dropbear"

LICENSE = "MIT"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    packagegroup-core-clutter-core \
    "

inherit core-image

QB_MEM = '${@bb.utils.contains("DISTRO_FEATURES", "opengl", "-m 512", "-m 256", d)}'
