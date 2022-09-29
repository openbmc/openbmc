EDK2_PLATFORM      = "ArmJuno"
EDK2_PLATFORM_DSC  = "Platform/ARM/JunoPkg/ArmJuno.dsc"
EDK2_BIN_NAME      = "BL33_AP_UEFI.fd"

COMPATIBLE_MACHINE = "juno"

# As of 2022-06-14 with 2022.05, clang builds fail:
# "The required fv image size 0x104048 exceeds the set fv image size 0xf9000"
TOOLCHAIN = "gcc"
