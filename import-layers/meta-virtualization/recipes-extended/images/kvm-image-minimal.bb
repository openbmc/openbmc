DESCRIPTION = "A minimal kvm image"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    qemu \
    libvirt \
    libvirt-libvirtd \
    libvirt-virsh \
    kernel-module-kvm \
    kernel-module-kvm-intel \
    kernel-module-kvm-amd \
    "

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_LINGUAS = " "

LICENSE = "MIT"

inherit core-image

IMAGE_ROOTFS_SIZE = "8192"
