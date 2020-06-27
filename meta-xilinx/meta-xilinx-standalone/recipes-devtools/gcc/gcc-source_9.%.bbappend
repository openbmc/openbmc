COMPATIBLE_HOST = "${HOST_SYS}"

# Add MicroBlaze Patches (only when using MicroBlaze)
FILESEXTRAPATHS_append_microblaze_xilinx-standalone := ":${THISDIR}/gcc-9"
SRC_URI_append_microblaze_xilinx-standalone = " \
        file://additional-microblaze-multilibs.patch \
"
