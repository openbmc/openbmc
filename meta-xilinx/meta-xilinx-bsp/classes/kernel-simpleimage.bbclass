python __anonymous () {
    kerneltypes = set((d.getVar("KERNEL_IMAGETYPE") or "").split())
    kerneltypes |= set((d.getVar("KERNEL_IMAGETYPES") or "").split())
    if any(t.startswith("simpleImage.") for t in kerneltypes):
        # Enable building of simpleImage
        bb.build.addtask('do_prep_simpleimage', 'do_compile', 'do_configure', d)
    uarch = d.getVar("UBOOT_ARCH")
    if uarch == "microblaze":
        d.appendVarFlag('do_prep_simpleimage', 'depends', ' virtual/dtb:do_populate_sysroot')
}

do_prep_simpleimage[dirs] += "${B}"
do_prep_simpleimage () {
    install -d ${B}/arch/${ARCH}/boot/dts
    for type in ${KERNEL_IMAGETYPES} ; do
        if [ -z "${type##*simpleImage*}" ] && [ ${ARCH} = "microblaze" ]; then
            ext="${type##*.}"
            # Microblaze simpleImage only works with dts file
            cp ${RECIPE_SYSROOT}/boot/devicetree/${ext}.dts ${B}/arch/${ARCH}/boot/dts/
        fi
    done
}

do_deploy_append () {
    for type in ${KERNEL_IMAGETYPES} ; do
        if [ -z "${type##*simpleImage*}" ] && [ ${ARCH} = "microblaze" ]; then
            base_name=${imageType}-${KERNEL_IMAGE_NAME}
            install -m 0644 ${KERNEL_OUTPUT_DIR}/${type}.strip $deployDir/${base_name}.strip
            install -m 0644 ${KERNEL_OUTPUT_DIR}/${type}.unstrip $deployDir/${base_name}.unstrip
            symlink_name=${imageType}-${KERNEL_IMAGE_LINK_NAME}
            ln -sf ${base_name}.strip $deployDir/${symlink_name}.strip
            ln -sf ${base_name}.unstrip $deployDir/${symlink_name}.unstrip
        fi
    done
}
