COMPATIBLE_MACHINE = "qemuarm-secureboot"

TFA_PLATFORM = "qemu"

# EDK2 dropped support for 32bit Arm, so u-boot only
TFA_UBOOT = "1"
TFA_INSTALL_TARGET = "flash.bin"

do_compile:append() {
    # Create a secure flash image for booting AArch64 Qemu. See:
    # https://trustedfirmware-a.readthedocs.io/en/latest/plat/qemu.html
    dd if=${BUILD_DIR}/bl1.bin of=${BUILD_DIR}/flash.bin bs=4096 conv=notrunc
    dd if=${BUILD_DIR}/fip.bin of=${BUILD_DIR}/flash.bin seek=64 bs=4096 conv=notrunc
}

do_deploy:append(){
    # runqemu requires flash.bin to be in the deploy directory
    ln -srn ${DEPLOYDIR}/${PN}/flash.bin ${DEPLOYDIR}/flash.bin
}
