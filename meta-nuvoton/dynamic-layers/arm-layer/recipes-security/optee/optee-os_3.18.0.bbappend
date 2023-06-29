SRC_URI:remove:npcm8xx = "git://github.com/OP-TEE/optee_os.git;branch=master;protocol=https"

SRC_URI:remove:npcm8xx = "file://0003-core-link-add-no-warn-rwx-segments.patch"
SRC_URI:remove:npcm8xx = "file://0004-core-Define-section-attributes-for-clang.patch"
SRC_URI:remove:npcm8xx = "file://0005-core-ldelf-link-add-z-execstack.patch"
SRC_URI:remove:npcm8xx = "file://0006-arm32-libutils-libutee-ta-add-.note.GNU-stack-sectio.patch"

SRC_URI:append:npcm8xx = "git://github.com/Nuvoton-Israel/optee_os.git;branch=npcm_3_18;protocol=https"

SRCREV:npcm8xx = "57e44ae6b3d6de756da8652ec132ffd7005439b7"

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
