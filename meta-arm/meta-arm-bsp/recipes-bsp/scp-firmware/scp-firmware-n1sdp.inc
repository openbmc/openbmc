# N1SDP specific SCP configurations and build instructions

COMPATIBLE_MACHINE:n1sdp = "n1sdp"

SCP_LOG_LEVEL = "INFO"

DEPENDS += "fiptool-native"
DEPENDS += "trusted-firmware-a"
DEPENDS += "n1sdp-board-firmware"

# The n1sdp sensor library is needed for building SCP N1SDP Platform
# https://github.com/ARM-software/SCP-firmware/tree/master/product/n1sdp
EXTRA_OECMAKE:append = " \
    -DSCP_N1SDP_SENSOR_LIB_PATH=${RECIPE_SYSROOT}/n1sdp-board-firmware_source/LIB/sensor.a \
"

do_install:append() {
   fiptool \
       create \
       --scp-fw "${D}/firmware/scp_ramfw.bin" \
       --blob uuid=cfacc2c4-15e8-4668-82be-430a38fad705,file="${RECIPE_SYSROOT}/firmware/bl1.bin" \
       "scp_fw.bin"

   # This UUID is FIP_UUID_MCP_BL2 in SCP-Firmware.
   fiptool \
       create \
       --blob uuid=54464222-a4cf-4bf8-b1b6-cee7dade539e,file="${D}/firmware/mcp_ramfw.bin" \
       "mcp_fw.bin"

   install "scp_fw.bin" "${D}/firmware/scp_fw.bin"
   install "mcp_fw.bin" "${D}/firmware/mcp_fw.bin"

   ln -sf "scp_romfw.bin" "${D}/firmware/scp_rom.bin"
   ln -sf "mcp_romfw.bin" "${D}/firmware/mcp_rom.bin"
}
