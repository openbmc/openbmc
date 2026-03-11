COMPATIBLE_MACHINE:sbsa-ref = "sbsa-ref"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://0001-BootLogoLib-align-logo-coords-to-be-even.patch"
# Need to use git as the BootLogo fix includes a binary patch, which quilt cannot handle
PATCHTOOL = "git"

DEPENDS:append:sbsa-ref     = " trusted-firmware-a coreutils-native"

EDK2_PLATFORM:sbsa-ref      = "SbsaQemu"
EDK2_PLATFORM_DSC:sbsa-ref  = "Platform/Qemu/SbsaQemu/SbsaQemu.dsc"
EDK2_BIN_NAME:sbsa-ref      = "SBSA_FLASH0.fd"

do_compile:prepend:sbsa-ref() {
    mkdir -p ${B}/Platform/Qemu/Sbsa/
    cp ${RECIPE_SYSROOT}/firmware/bl1.bin ${B}/Platform/Qemu/Sbsa/
    cp ${RECIPE_SYSROOT}/firmware/fip.bin ${B}/Platform/Qemu/Sbsa/
}

do_install:append:sbsa-ref() {
    install ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/SBSA_FLASH*.fd ${D}/firmware/
    # QEMU requires that the images be minimum of 256M in size
    truncate -s 256M ${D}/firmware/SBSA_FLASH*.fd
}

