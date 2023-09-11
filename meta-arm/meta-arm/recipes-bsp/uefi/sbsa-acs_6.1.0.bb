require recipes-bsp/uefi/edk2-firmware_202302.bb
PROVIDES:remove = "virtual/bootloader"

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://ShellPkg/Application/sbsa-acs/LICENSE.md;md5=2a944942e1496af1886903d274dedb13"

SRC_URI += "git://github.com/ARM-software/sbsa-acs;destsuffix=edk2/ShellPkg/Application/sbsa-acs;protocol=https;branch=master;name=acs \
            git://github.com/tianocore/edk2-libc;destsuffix=edk2/edk2-libc;protocol=https;branch=master;name=libc \
            file://0002-Patch-in-the-paths-to-the-SBSA-test-suite.patch \
            file://0003-Enforce-using-good-old-BFD-linker.patch \
            file://0001-Fix-function-protype-mismatches.patch;patchdir=ShellPkg/Application/sbsa-acs \
            file://0001-Fix-for-issue-245.patch;patchdir=ShellPkg/Application/sbsa-acs \
            "


SRCREV_acs = "7d7a3fe81ad7e6f05143ba17db50107f1ab6c9cd"
SRCREV_libc = "a806ea1062c254bd6e09db7d0f7beb4d14bc3ed0"

# GCC12 trips on it
#see https://src.fedoraproject.org/rpms/edk2/blob/rawhide/f/0032-Basetools-turn-off-gcc12-warning.patch
BUILD_CFLAGS += "-Wno-error=stringop-overflow"

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_MACHINE = ""
PACKAGE_ARCH = "${TUNE_PKGARCH}"

EDK2_PLATFORM = "Shell"
EDK2_PLATFORM_DSC = "ShellPkg/ShellPkg.dsc"
EDK2_EXTRA_BUILD = "--module ShellPkg/Application/sbsa-acs/uefi_app/SbsaAvs.inf"

PACKAGES_PATH .= ":${S}/edk2-libc"

do_install() {
    install -d ${D}/firmware
    install ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/*/Sbsa.efi ${D}/firmware/
}
