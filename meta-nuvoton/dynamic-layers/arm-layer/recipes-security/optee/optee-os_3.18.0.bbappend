SRC_URI:remove:npcm8xx = "git://github.com/OP-TEE/optee_os.git;branch=master;protocol=https"

SRC_URI:remove:npcm8xx = " \
    file://0006-allow-setting-sysroot-for-libgcc-lookup.patch \
   "

SRC_URI:append:npcm8xx = "git://github.com/Nuvoton-Israel/optee_os.git;branch=npcm_3_18;protocol=https"

SRCREV:npcm8xx = "6bac40e18433a1c0cf219d994cc33610899febe7"

EXTRA_OEMAKE:append:npcm8xx = " \
    CFG_REE_FS=n \
    CFG_REE_FS_TA=n \
    CFG_RPMB_FS=y \
    CFG_RPMB_TESTKEY=y \
    CFG_RPMB_WRITE_KEY=y \
    CFG_CORE_HEAP_SIZE=524288 \
    CFG_TEE_RAM_VA_SIZE=3145728 \
    "

do_deploy:npcm8xx() {
    install -d ${DEPLOYDIR}/
    install -m 644 ${D}${nonarch_base_libdir}/firmware/* ${DEPLOYDIR}/
}
