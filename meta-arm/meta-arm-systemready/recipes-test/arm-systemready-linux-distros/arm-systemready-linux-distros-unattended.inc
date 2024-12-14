SUMMARY = "Arm SystemReady Linux distros unattended requirements"
DESCRIPTION = "Arm SystemReady Linux distro unattended configurations \
               and ISO image modification"

EXTRACTED_ISO_TEMP_DIR = "${WORKDIR}/extracted_iso_temp_dir"
NEW_ISO_TEMP_DIR = "${WORKDIR}/new_iso_temp_dir"

# oeqa test case must be added to TEST_SUITES to acknowledge that the unattended
# installation was successful.
inherit testimage

python () {
    unattended_required_vars = ['ISO_LABEL', 'BOOT_CATALOG', 'BOOT_IMAGE', 'EFI_IMAGE']

    for var in unattended_required_vars:
        if not d.getVar(var):
            raise bb.parse.SkipRecipe(f'{var} variable is not set')
}

unpackiso() {
    # Unpack the ISO image
    bsdtar -xf ${UNPACKDIR}/${ISO_IMAGE_NAME}.iso -C ${EXTRACTED_ISO_TEMP_DIR}
    chmod -R u+rw ${EXTRACTED_ISO_TEMP_DIR}
}

modifyiso() {

}

repackiso() {
    # Repack the ISO image
    mkisofs -o ${NEW_ISO_TEMP_DIR}/${ISO_IMAGE_NAME}.iso -U -r -v -T -J -joliet-long -V ${ISO_LABEL} \
            -volset ${ISO_LABEL} -A ${ISO_LABEL} -b ${BOOT_IMAGE} -c ${BOOT_CATALOG} -no-emul-boot \
            -boot-load-size 4 -boot-info-table -J -R -V ${ISO_LABEL} -eltorito-alt-boot \
            -eltorito-boot ${EFI_IMAGE} -no-emul-boot ${EXTRACTED_ISO_TEMP_DIR}

    mv -f ${NEW_ISO_TEMP_DIR}/${ISO_IMAGE_NAME}.iso ${UNPACKDIR}
}

# Write the test data in IMAGE_POSTPROCESS_COMMAND
IMAGE_POSTPROCESS_COMMAND += "write_image_test_data; "

do_unpack[depends] += "cdrtools-native:do_populate_sysroot libarchive-native:do_populate_sysroot"
do_unpack[postfuncs] += "unpackiso modifyiso repackiso"
do_unpack[cleandirs] += "${EXTRACTED_ISO_TEMP_DIR} ${NEW_ISO_TEMP_DIR}"