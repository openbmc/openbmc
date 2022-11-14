EXTRA_OEMAKE:append:npcm8xx = " \
    TA_CPU=cortex-a35+crypto \
    CFG_TA_DEBUG=n \
    CFG_TEE_TA_LOG_LEVEL=0 \
    CFG_ARM64_ta_arm64=y \
    "