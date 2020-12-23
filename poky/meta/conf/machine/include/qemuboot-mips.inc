# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_MACHINE = "-machine malta"
QB_KERNEL_CMDLINE_APPEND = "console=ttyS0 console=tty"
QB_OPT_APPEND = "-usb -device usb-tablet"

# For graphics to work we need to define the VGA device as well as the necessary USB devices
QB_OPT_APPEND += "-vga std"
