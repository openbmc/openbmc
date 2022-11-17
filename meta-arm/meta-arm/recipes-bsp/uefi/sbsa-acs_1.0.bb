require recipes-bsp/uefi/edk2-firmware_202205.bb
PROVIDES:remove = "virtual/bootloader"

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://ShellPkg/Application/sbsa-acs/LICENSE.md;md5=2a944942e1496af1886903d274dedb13"

SRC_URI += "git://github.com/ARM-software/sbsa-acs;destsuffix=edk2/ShellPkg/Application/sbsa-acs;protocol=https;branch=master;name=acs \
            git://github.com/tianocore/edk2-libc;destsuffix=edk2/edk2-libc;protocol=https;branch=master;name=libc \
            file://shell.patch \
            file://use_bfd_linker.patch \
            file://0001-Revert-peripheral-test-3-updated-for-multiple-uarts.patch;patchdir=ShellPkg/Application/sbsa-acs \
            "

SRCREV_acs = "28ecef569303af18b571ff3d66bbdcb6135eaed8"
SRCREV_libc = "c32222fed9927420fc46da503dea1ebb874698b6"

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
