OBMC_IMAGE_EXTRA_INSTALL:append = "\
    biosconfig-manager \
    mctp \
    platform-init \
"

OBMC_IMAGE_EXTRA_INSTALL:append:nv-gpu-pcie-card = "\
    gpu-card-init \
"
