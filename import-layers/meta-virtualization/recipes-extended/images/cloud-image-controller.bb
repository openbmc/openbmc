IMAGE_FEATURES += "ssh-server-openssh package-management"
EXTRA_IMAGE_FEATURES = "tools-debug debug-tweaks"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} \
    packagegroup-core-basic \
    openvswitch \
    openvswitch-controller \
    openvswitch-switch \
    openvswitch-brcompat \
    criu \
    libvirt \
    libvirt-libvirtd \
    libvirt-python \
    libvirt-virsh \    
    openflow \
    qemu \   
    kernel-modules \
    dhcp-client \
    perl-modules \
    grub \
    mysql5 \
    python-twisted \ 
    python-lxml \
    "

inherit core-image
inherit image-vm

IMAGE_FSTYPES = "vmdk tar.gz"

# Ensure extra space for guest images
#IMAGE_ROOTFS_EXTRA_SPACE = "41943040"
