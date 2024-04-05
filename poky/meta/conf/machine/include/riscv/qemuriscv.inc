PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"

require conf/machine/include/qemu.inc
require conf/machine/include/riscv/tune-riscv.inc

MACHINE_FEATURES += "keyboard ext2 ext3 serial"

KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES += "uImage"
KEEPUIMAGE = "no"

SERIAL_CONSOLES ?= "115200;ttyS0 115200;hvc0"

IMAGE_FSTYPES += "ext4 wic.qcow2"

WKS_FILE ?= "qemuriscv.wks"

MACHINE_EXTRA_RRECOMMENDS += " kernel-modules"

EXTRA_IMAGEDEPENDS += "opensbi"
RISCV_SBI_PLAT ?= "generic"
RISCV_SBI_PAYLOAD ?= "${KERNEL_IMAGETYPE}-${MACHINE}.bin"

UBOOT_ENTRYPOINT:riscv32 = "0x80400000"
UBOOT_ENTRYPOINT:riscv64 = "0x80200000"

# qemuboot options
QB_SMP ?= "-smp 4"
QB_KERNEL_CMDLINE_APPEND = "earlycon=sbi"
QB_MACHINE = "-machine virt"
QB_DEFAULT_BIOS = "fw_jump.elf"
QB_TAP_OPT = "-netdev tap,id=net0,ifname=@TAP@,script=no,downscript=no"
QB_NETWORK_DEVICE = "-device virtio-net-device,netdev=net0,mac=@MAC@"
QB_ROOTFS_OPT = "-drive id=disk0,file=@ROOTFS@,if=none,format=raw -device virtio-blk-device,drive=disk0"
QB_SERIAL_OPT = "-device virtio-serial-device -chardev null,id=virtcon -device virtconsole,chardev=virtcon"
QB_TCPSERIAL_OPT = " -device virtio-serial-device -chardev socket,id=virtcon,port=@PORT@,host=127.0.0.1,nodelay=on -device virtconsole,chardev=virtcon"
QB_GRAPHICS = "-device bochs-display"
QB_OPT_APPEND = "-device qemu-xhci -device usb-tablet -device usb-kbd"
QB_OPT_APPEND:riscv32 = "-device virtio-tablet-pci -device virtio-keyboard-pci"
