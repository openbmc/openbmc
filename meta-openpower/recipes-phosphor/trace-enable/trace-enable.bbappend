TRACE_EVENTS:append = " \
    fsi_slave_init \
    fsi_slave_invalid_cfam \
    fsi_dev_init \
    fsi_master_aspeed_cfam_reset \
    fsi_master_scan \
    fsi_master_unregister \
"
TRACE_EVENTS:append:p10bmc = " \
    i2cr_i2c_error \
    i2cr_status_error \
    xdma_start \
    xdma_irq \
    xdma_reset \
    xdma_perst \
    xdma_unmap \
    xdma_mmap_error \
    xdma_mmap \
"
