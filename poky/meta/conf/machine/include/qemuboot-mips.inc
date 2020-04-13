# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_MACHINE = "-machine malta"
QB_KERNEL_CMDLINE_APPEND = "console=ttyS0 console=tty"
QB_OPT_APPEND = "-show-cursor -usb -device usb-tablet"
# Add the 'virtio-rng-pci' device otherwise the guest may run out of entropy
QB_OPT_APPEND += "-object rng-random,filename=/dev/urandom,id=rng0 -device virtio-rng-pci,rng=rng0"

# For graphics to work we need to define the VGA device as well as the necessary USB devices
QB_OPT_APPEND += "-vga std"
