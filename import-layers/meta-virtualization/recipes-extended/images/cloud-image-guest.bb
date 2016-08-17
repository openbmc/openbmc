IMAGE_FEATURES += "ssh-server-openssh package-management"
EXTRA_IMAGE_FEATURES = "tools-debug debug-tweaks"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} \
    packagegroup-core-basic \
    openflow \
    qemu \   
    kernel-modules \
    tcpdump \
    dhcp-client \
    "

inherit core-image
inherit image-vm

IMAGE_FSTYPES += "vmdk"
