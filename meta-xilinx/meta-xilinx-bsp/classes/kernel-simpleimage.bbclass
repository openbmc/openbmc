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
        if [[ "${type}" =~ "simpleImage" ]] && [ ${ARCH} = "microblaze" ]; then
            ext="${type##*.}"
            cp ${RECIPE_SYSROOT}/boot/devicetree/${ext}.dtb ${B}/arch/${ARCH}/boot/dts/
        fi
    done
}

