TRACE_EVENTS:append = " \
    fsi_slave_init \
    fsi_slave_invalid_cfam \
    fsi_dev_init \
    fsi_master_aspeed_cfam_reset \
"
TRACE_EVENTS:append:p10bmc = " \
    xdma_start \
    xdma_irq \
    xdma_reset \
    xdma_perst \
    xdma_unmap \
    xdma_mmap_error \
    xdma_mmap \
"
