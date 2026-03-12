ETHOSU_NUM_MACS ?= "256"

FVP_CONFIG[host.ethosu.num_macs] = "${ETHOSU_NUM_MACS}"

IMAGE_INSTALL:append = " arm-npu-ethosu"
