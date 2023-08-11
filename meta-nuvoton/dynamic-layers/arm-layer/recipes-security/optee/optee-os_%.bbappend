EXTRA_OEMAKE:append:npcm8xx = " \
    CFG_CORE_HEAP_SIZE=524288 \
    CFG_TEE_RAM_VA_SIZE=3145728 \
    CFG_TZDRAM_START=0x02100000 \
    CFG_TZDRAM_SIZE=0x03f00000 \
    CFG_SHMEM_START=0x06000000 \
    CFG_TEE_SDP_MEM_BASE=0x05F00000 \
    CFG_TEE_SDP_MEM_SIZE=0x00100000 \
    "

do_deploy:npcm8xx() {
    install -d ${DEPLOYDIR}/
    install -m 644 ${D}${nonarch_base_libdir}/firmware/* ${DEPLOYDIR}/
}
