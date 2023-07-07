SRCREV:npcm8xx = "a012b9923d52be5721f6046c0464bdf645d87b01"

SRC_URI:remove:npcm8xx = "file://0004-core-Define-section-attributes-for-clang.patch"
SRC_URI:remove:npcm8xx = "file://0005-core-arm-S-EL1-SPMC-boot-ABI-update.patch"
SRC_URI:remove:npcm8xx = "file://0006-core-ffa-add-TOS_FW_CONFIG-handling.patch"
SRC_URI:remove:npcm8xx = "file://0007-core-spmc-handle-non-secure-interrupts.patch"
SRC_URI:remove:npcm8xx = "file://0008-core-spmc-configure-SP-s-NS-interrupt-action-based-o.patch"

EXTRA_OEMAKE:append:npcm8xx = " \
    CFG_REE_FS=n \
    CFG_REE_FS_TA=n \
    CFG_RPMB_FS=y \
    CFG_RPMB_TESTKEY=y \
    CFG_RPMB_WRITE_KEY=y \
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
