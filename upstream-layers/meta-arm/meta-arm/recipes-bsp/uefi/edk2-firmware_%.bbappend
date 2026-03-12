COMPATIBLE_MACHINE:qemuarm64 = "qemuarm64"
EDK2_PLATFORM:qemuarm64      = "ArmVirtQemu-AArch64"
EDK2_PLATFORM_DSC:qemuarm64  = "ArmVirtPkg/ArmVirtQemu.dsc"
EDK2_BIN_NAME:qemuarm64      = "QEMU_EFI.fd"
# No need for PXE booting in qemu, disable to reduce unnecessary noise
EDK2_EXTRA_BUILD:qemuarm64 += " -D NETWORK_PXE_BOOT_ENABLE=FALSE "

COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64-secureboot"
EDK2_PLATFORM:qemuarm64-secureboot      = "ArmVirtQemuKernel-AArch64"
EDK2_PLATFORM_DSC:qemuarm64-secureboot  = "ArmVirtPkg/ArmVirtQemuKernel.dsc"
EDK2_BIN_NAME:qemuarm64-secureboot      = "QEMU_EFI.fd"
#EDK2_BUILD_RELEASE:qemuarm64-secureboot = "0"

do_install:append:qemuarm64() {
    install ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/${EDK2_BIN_NAME} ${D}/firmware/
}
