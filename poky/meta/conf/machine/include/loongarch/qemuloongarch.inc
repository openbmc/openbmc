PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"

require conf/machine/include/qemu.inc
require conf/machine/include/loongarch/tune-loongarch.inc

MACHINE_FEATURES = "screen keyboard ext2 ext3 serial"

KERNEL_IMAGETYPE = "vmlinuz"
KERNEL_IMAGETYPES += "vmlinuz"
KEEPUIMAGE = "no"

SERIAL_CONSOLES ?= "115200;ttyS0 115200;hvc0"

IMAGE_FSTYPES += "ext4 wic.qcow2"

WKS_FILE ?= "qemuloongarch.wks"

MACHINE_EXTRA_RRECOMMENDS += " kernel-modules"

#EXTRA_IMAGEDEPENDS += "opensbi"

UBOOT_ENTRYPOINT_loongarch32 = "0x80400000"
UBOOT_ENTRYPOINT_loongarch64 = "0x80200000"

# qemuboot options
QB_KERNEL_CMDLINE_APPEND = "earlycon=sbi"
QB_MACHINE = "-machine virt"
QB_DEFAULT_BIOS = "fw_jump.elf"
QB_TAP_OPT = "-netdev tap,id=net0,ifname=@TAP@,script=no,downscript=no"
QB_NETWORK_DEVICE = "-device virtio-net-device,netdev=net0,mac=@MAC@"
QB_ROOTFS_OPT = "-drive id=disk0,file=@ROOTFS@,if=none,format=raw -device virtio-blk-device,drive=disk0"
QB_SERIAL_OPT = "-device virtio-serial-pci -chardev null,id=virtcon -device virtconsole,chardev=virtcon"
QB_TCPSERIAL_OPT = " -device virtio-serial-pci -chardev socket,id=virtcon,port=@PORT@,host=127.0.0.1,nodelay=on -device virtconsole,chardev=virtcon"
# Add the 'virtio-rng-pci' device otherwise the guest may run out of entropy
QB_OPT_APPEND = " -object rng-random,filename=/dev/urandom,id=rng0 -device virtio-rng-device,rng=rng0"
