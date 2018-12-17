PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
PREFERRED_PROVIDER_virtual/egl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa"

XSERVER ?= "xserver-xorg \
            ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'mesa-driver-swrast xserver-xorg-extension-glx', '', d)} \
            xf86-video-fbdev \
            "

MACHINE_FEATURES = "alsa bluetooth usbgadget screen"

MACHINEOVERRIDES =. "qemuall:"

IMAGE_FSTYPES += "tar.bz2 ext4"

# Don't include kernels in standard images
RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""

# Use a common kernel recipe for all QEMU machines
PREFERRED_PROVIDER_virtual/kernel ??= "linux-yocto"

EXTRA_IMAGEDEPENDS += "qemu-native qemu-helper-native"

# Provide the nfs server kernel module for all qemu images
KERNEL_FEATURES_append_pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
KERNEL_FEATURES_append_pn-linux-yocto-rt = " features/nfsd/nfsd-enable.scc"

IMAGE_CLASSES += "qemuboot"
