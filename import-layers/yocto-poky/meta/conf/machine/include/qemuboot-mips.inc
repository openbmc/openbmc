# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_MEM = "-m 256"
QB_MACHINE = "-machine malta"
QB_KERNEL_CMDLINE_APPEND = "console=ttyS0 console=tty"
# Add the 'virtio-rng-pci' device otherwise the guest may run out of entropy
QB_OPT_APPEND = "-vga cirrus -show-cursor -usb -device usb-tablet -device virtio-rng-pci"
QB_SYSTEM_NAME = "qemu-system-${TUNE_ARCH}"
