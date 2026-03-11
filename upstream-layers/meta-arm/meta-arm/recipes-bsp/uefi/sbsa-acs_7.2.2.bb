require recipes-bsp/uefi/edk2-firmware_202505.bb
PROVIDES:remove = "virtual/bootloader"

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://ShellPkg/Application/sbsa-acs/LICENSE.md;md5=2a944942e1496af1886903d274dedb13"

SRC_URI += "git://github.com/ARM-software/sbsa-acs;destsuffix=edk2/ShellPkg/Application/sbsa-acs;protocol=https;branch=master;name=acs \
            git://github.com/tianocore/edk2-libc;destsuffix=edk2/edk2-libc;protocol=https;branch=master;name=libc \
            git://github.com/ARM-software/bsa-acs.git;destsuffix=edk2/ShellPkg/Application/bsa-acs;protocol=https;branch=main;name=bsa \
            file://0001-Patch-in-the-paths-to-the-SBSA-test-suite.patch \
            file://0002-Enforce-using-good-old-BFD-linker.patch \
            file://0001-Bug-fix-for-GCC15-incompatible-pointer-type-errors.patch;patchdir=ShellPkg/Application/bsa-acs \
            "

# v25.03_REL7.2.2 tag
SRCREV_acs = "642bc12bbc3b526f3931eca580002430c162f2a2"
# v25.03_REL1.1.0 tag
SRCREV_bsa = "db423614002621e1f0a9440955e3503623ff64f3"
# v3.6.8.1 tag
SRCREV_libc = "caea801aac338aa60f85a7c10148ca0b4440fff3"

UPSTREAM_CHECK_URI = "https://github.com/ARM-software/sbsa-acs/releases"

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_MACHINE = ""
PACKAGE_ARCH = "${TUNE_PKGARCH}"

EDK2_PLATFORM = "Shell"
EDK2_PLATFORM_DSC = "ShellPkg/ShellPkg.dsc"
EDK2_EXTRA_BUILD = "--module ShellPkg/Application/sbsa-acs/uefi_app/SbsaAvs.inf"

export BSA_PATH = "${S}/ShellPkg/Application/bsa-acs"
PACKAGES_PATH .= ":${S}/edk2-libc"

do_install() {
    install -d ${D}/firmware
    install ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/*/Sbsa.efi ${D}/firmware/
}
