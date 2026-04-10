# RD N2 specific EDK2 configurations
EDK2_BUILD_RELEASE = "0"
EDK2_PLATFORM      = "RdN2"
EDK2_PLATFORM_DSC  = "Platform/ARM/SgiPkg/RdN2/RdN2.dsc"
EDK2_BIN_NAME      = "BL33_AP_UEFI.fd"

COMPATIBLE_MACHINE = "rdn2"

SRC_URI_EDK2 = "gitsm://gitlab.arm.com/infra-solutions/reference-design/platsw/edk2.git;protocol=https"
SRC_URI_EDK2_PLATFORMS = "git://gitlab.arm.com/infra-solutions/reference-design/platsw/edk2-platforms.git;protocol=https"

SRCBRANCH_edk2 = "refinfra"
SRCBRANCH_edk2_platforms = "refinfra"

# Use the RD-INFRA-2024.12.20 tag
SRCREV_edk2           = "dd25905d63ec72d6e06804155fda1098d10da19e"
SRCREV_edk2-platforms = "f6ed3b0f92eeaf380638f77d8b5ace805facb301"
