XSERVER ?= "xserver-xorg \
            ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'mesa-driver-swrast xserver-xorg-extension-glx', '', d)} \
            xf86-video-fbdev \
            xf86-video-modesetting \
            "

MACHINE_FEATURES = "alsa bluetooth usbgadget screen vfat"

MACHINEOVERRIDES =. "qemuall:"

IMAGE_FSTYPES += "tar.bz2 ext4"

# Don't include kernels in standard images
RDEPENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Use a common kernel recipe for all QEMU machines
PREFERRED_PROVIDER_virtual/kernel ??= "linux-yocto"

# Provide the nfs server kernel module for all qemu images
KERNEL_FEATURES:append:pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
KERNEL_FEATURES:append:pn-linux-yocto-rt = " features/nfsd/nfsd-enable.scc"

IMAGE_CLASSES += "qemuboot"

# Use gnu tar so we can handle sparse files in testimage debug archives
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "tar"
