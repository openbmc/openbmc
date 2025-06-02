OBMC_IMAGE_EXTRA_INSTALL:append = "\
    biosconfig-manager \
    mctp \
    platform-init \
    smbios-mdr \
"

OBMC_IMAGE_EXTRA_INSTALL:append:nv-gpu-pcie-card = "\
    gpu-card-init \
"
