# This class should be included by any recipe that wants to access or provide
# the platform init source files which are used to initialize a Zynq or ZynqMP
# SoC.

# Define the path to the xilinx platform init code/headers
PLATFORM_INIT_DIR ?= "/usr/src/xilinx-platform-init"

PLATFORM_INIT_STAGE_DIR = "${STAGING_DIR_HOST}${PLATFORM_INIT_DIR}"

# Target files use for platform init
PLATFORM_INIT_FILES ?= ""
PLATFORM_INIT_FILES_zynq = "ps7_init_gpl.c ps7_init_gpl.h"
PLATFORM_INIT_FILES_zynqmp = "psu_init_gpl.c psu_init_gpl.h"

