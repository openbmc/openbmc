COMPATIBLE_HOST = "${HOST_SYS}"

# Add MicroBlaze Patches (only when using MicroBlaze)
FILESEXTRAPATHS_append_microblaze_xilinx-standalone := ":${THISDIR}/gcc-10"
SRC_URI_append_microblaze_xilinx-standalone = " \
        file://additional-microblaze-multilibs.patch \
"

CHECK_FOR_MICROBLAZE_microblaze = "1"

python() {
    if d.getVar('CHECK_FOR_MICROBLAZE') == '1':
        if 'xilinx-microblaze' not in d.getVar('BBFILE_COLLECTIONS').split():
            bb.fatal('You must include the meta-microblaze layer to build for this configuration.')
}
