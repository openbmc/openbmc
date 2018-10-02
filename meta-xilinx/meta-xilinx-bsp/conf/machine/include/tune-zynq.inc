DEFAULTTUNE ?= "cortexa9thf-neon"
SOC_FAMILY ?= "zynq"

# Available SOC_VARIANT's for zynq:
# 7zs - Zynq-7000 Single A9 Core
# 7z  - Zynq-7000 Dual A9 Core

SOC_VARIANT ?= "7z"

require conf/machine/include/tune-cortexa9.inc
require conf/machine/include/soc-family.inc

# Linux Configuration
KERNEL_IMAGETYPE ?= "uImage"

# Set default load address.
# Override with KERNEL_EXTRA_ARGS_<board> += "..." in machine file if required
KERNEL_EXTRA_ARGS_zynq += "UIMAGE_LOADADDR=0x8000"
