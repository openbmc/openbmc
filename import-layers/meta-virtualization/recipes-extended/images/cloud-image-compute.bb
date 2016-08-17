IMAGE_FEATURES += "ssh-server-openssh"
EXTRA_IMAGE_FEATURES = "tools-debug debug-tweaks"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    packagegroup-core-basic \
    openvswitch \
    libvirt \
    openflow \
    "

inherit core-image

IMAGE_FSTYPES = "tar.gz"
