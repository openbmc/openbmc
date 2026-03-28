require recipes-bsp/scp-firmware/scp-firmware.inc

SRCBRANCH = "main"
SRCREV  = "190e938c2da3631b4834a90448516423099c79f7"
SRC_URI += "file://0001-OPTEE-Private-Includes.patch"

# For now we only build with GCC, so stop meta-clang trying to get involved
TOOLCHAIN = "gcc"
