
inherit kernel-arch kernel-artifact-names uboot-config deploy
require conf/image-fitimage.conf

S = "${UNPACKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# This bbclass requires KERNEL_CLASSES += "kernel-fit-extra-artifacts"
EXCLUDE_FROM_WORLD = "1"

DEPENDS += "\
    u-boot-tools-native dtc-native \
    ${@'kernel-signing-keys-native' if d.getVar('FIT_GENERATE_KEYS') == '1' else ''} \
"

python () {
    image = d.getVar('INITRAMFS_IMAGE')
    if image and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1':
        if d.getVar('INITRAMFS_MULTICONFIG'):
            mc = d.getVar('BB_CURRENT_MC')
            d.appendVarFlag('do_compile', 'mcdepends', ' mc:' + mc + ':${INITRAMFS_MULTICONFIG}:${INITRAMFS_IMAGE}:do_image_complete')
        else:
            d.appendVarFlag('do_compile', 'depends', ' ${INITRAMFS_IMAGE}:do_image_complete')

    #check if there are any dtb providers
    providerdtb = d.getVar("PREFERRED_PROVIDER_virtual/dtb")
    if providerdtb:
        d.appendVarFlag('do_compile', 'depends', ' virtual/dtb:do_populate_sysroot')
        d.setVar('EXTERNAL_KERNEL_DEVICETREE', "${RECIPE_SYSROOT}/boot/devicetree")
}

do_configure[noexec] = "1"

UBOOT_MKIMAGE_KERNEL_TYPE ?= "kernel"
KERNEL_IMAGEDEST ?= "/boot"

python do_compile() {
    import shutil
    import oe.fitimage

    itsfile = "fit-image.its"
    fitname = "fitImage"
    kernel_deploydir = d.getVar('DEPLOY_DIR_IMAGE')
    kernel_deploysubdir = d.getVar('KERNEL_DEPLOYSUBDIR')
    if kernel_deploysubdir:
        kernel_deploydir = os.path.join(kernel_deploydir, kernel_deploysubdir)

    # Collect all the its nodes before the its file is generated and mkimage gets executed
    root_node = oe.fitimage.ItsNodeRootKernel(
        d.getVar("FIT_DESC"), d.getVar("FIT_ADDRESS_CELLS"),
        d.getVar('HOST_PREFIX'), d.getVar('UBOOT_ARCH'),  d.getVar("FIT_CONF_PREFIX"),
        oe.types.boolean(d.getVar('UBOOT_SIGN_ENABLE')), d.getVar("UBOOT_SIGN_KEYDIR"),
        d.getVar("UBOOT_MKIMAGE"), d.getVar("UBOOT_MKIMAGE_DTCOPTS"),
        d.getVar("UBOOT_MKIMAGE_SIGN"), d.getVar("UBOOT_MKIMAGE_SIGN_ARGS"),
        d.getVar('FIT_HASH_ALG'), d.getVar('FIT_SIGN_ALG'), d.getVar('FIT_PAD_ALG'),
        d.getVar('UBOOT_SIGN_KEYNAME'),
        oe.types.boolean(d.getVar('FIT_SIGN_INDIVIDUAL')), d.getVar('UBOOT_SIGN_IMG_KEYNAME')
    )

    # Prepare a kernel image section.
    shutil.copyfile(os.path.join(kernel_deploydir, "linux.bin"), "linux.bin")
    with open(os.path.join(kernel_deploydir, "linux_comp")) as linux_comp_f:
        linux_comp = linux_comp_f.read()
    root_node.fitimage_emit_section_kernel("kernel-1", "linux.bin", linux_comp,
        d.getVar('UBOOT_LOADADDRESS'), d.getVar('UBOOT_ENTRYPOINT'),
        d.getVar('UBOOT_MKIMAGE_KERNEL_TYPE'), d.getVar("UBOOT_ENTRYSYMBOL"))

    # Prepare a DTB image section
    kernel_devicetree = d.getVar('KERNEL_DEVICETREE')
    external_kernel_devicetree = d.getVar("EXTERNAL_KERNEL_DEVICETREE")
    if kernel_devicetree:
        for dtb in kernel_devicetree.split():
            # In deploy_dir the DTBs are without sub-directories also with KERNEL_DTBVENDORED = "1"
            dtb_name = os.path.basename(dtb)

            # Skip DTB if it's also provided in EXTERNAL_KERNEL_DEVICETREE directory
            if external_kernel_devicetree:
                ext_dtb_path = os.path.join(external_kernel_devicetree, dtb_name)
                if os.path.exists(ext_dtb_path) and os.path.getsize(ext_dtb_path) > 0:
                    continue

            # Copy the dtb or dtbo file into the FIT image assembly directory
            shutil.copyfile(os.path.join(kernel_deploydir, dtb_name), dtb_name)
            root_node.fitimage_emit_section_dtb(dtb_name, dtb_name,
                d.getVar("UBOOT_DTB_LOADADDRESS"), d.getVar("UBOOT_DTBO_LOADADDRESS"))

    if external_kernel_devicetree:
        # iterate over all .dtb and .dtbo files in the external kernel devicetree directory
        # and copy them to the FIT image assembly directory
        for dtb_name in sorted(os.listdir(external_kernel_devicetree)):
            if dtb_name.endswith('.dtb') or dtb_name.endswith('.dtbo'):
                dtb_path = os.path.join(external_kernel_devicetree, dtb_name)

                # For symlinks, add a configuration node that refers to the DTB image node to which the symlink points
                symlink_target = oe.fitimage.symlink_points_below(dtb_name, external_kernel_devicetree)
                if symlink_target:
                    root_node.fitimage_emit_section_dtb_alias(dtb_name, symlink_target, True)
                # For real DTB files add an image node and a configuration node
                else:
                    shutil.copyfile(dtb_path, dtb_name)
                    root_node.fitimage_emit_section_dtb(dtb_name, dtb_name,
                        d.getVar("UBOOT_DTB_LOADADDRESS"), d.getVar("UBOOT_DTBO_LOADADDRESS"), True)

    # Prepare a u-boot script section
    fit_uboot_env = d.getVar("FIT_UBOOT_ENV")
    if fit_uboot_env:
        root_node.fitimage_emit_section_boot_script("bootscr-"+fit_uboot_env , fit_uboot_env)

    # Prepare a setup section (For x86)
    setup_bin_path = os.path.join(kernel_deploydir, "setup.bin")
    if os.path.exists(setup_bin_path):
        shutil.copyfile(setup_bin_path, "setup.bin")
        root_node.fitimage_emit_section_setup("setup-1", "setup.bin")

    # Prepare a ramdisk section.
    initramfs_image = d.getVar('INITRAMFS_IMAGE')
    if initramfs_image and d.getVar("INITRAMFS_IMAGE_BUNDLE") != '1':
        # Find and use the first initramfs image archive type we find
        found = False
        for img in d.getVar("FIT_SUPPORTED_INITRAMFS_FSTYPES").split():
            initramfs_path = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"), "%s.%s" % (d.getVar('INITRAMFS_IMAGE_NAME'), img))
            if os.path.exists(initramfs_path):
                bb.note("Found initramfs image: " + initramfs_path)
                found = True
                root_node.fitimage_emit_section_ramdisk("ramdisk-1", initramfs_path,
                    initramfs_image,
                    d.getVar("UBOOT_RD_LOADADDRESS"),
                    d.getVar("UBOOT_RD_ENTRYPOINT"))
                break
            else:
                bb.note("Did not find initramfs image: " + initramfs_path)

        if not found:
            bb.fatal("Could not find a valid initramfs type for %s, the supported types are: %s" % (d.getVar('INITRAMFS_IMAGE_NAME'), d.getVar('FIT_SUPPORTED_INITRAMFS_FSTYPES')))

    # Generate the configuration section
    root_node.fitimage_emit_section_config(d.getVar("FIT_CONF_DEFAULT_DTB"))

    # Write the its file
    root_node.write_its_file(itsfile)

    # Assemble the FIT image
    root_node.run_mkimage_assemble(itsfile, fitname)

    # Sign the FIT image if required
    root_node.run_mkimage_sign(fitname)
}
do_compile[depends] += "virtual/kernel:do_deploy"

do_install() {
    install -d "${D}/${KERNEL_IMAGEDEST}"
    install -m 0644 "${B}/fitImage" "${D}/${KERNEL_IMAGEDEST}/fitImage"
}

FILES:${PN} = "${KERNEL_IMAGEDEST}"


do_deploy() {
    deploy_dir="${DEPLOYDIR}"
    if [ -n "${KERNEL_DEPLOYSUBDIR}" ]; then
        deploy_dir="${DEPLOYDIR}/${KERNEL_DEPLOYSUBDIR}"
    fi
    install -d "$deploy_dir"
    install -m 0644 "${B}/fitImage" "$deploy_dir/fitImage"
    install -m 0644 "${B}/fit-image.its" "$deploy_dir/fit-image.its"

    if [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        ln -snf fit-image.its "$deploy_dir/fitImage-its-${KERNEL_FIT_NAME}.its"
        if [ -n "${KERNEL_FIT_LINK_NAME}" ] ; then
            ln -snf fit-image.its "$deploy_dir/fitImage-its-${KERNEL_FIT_LINK_NAME}"
        fi
    fi

    if [ -n "${INITRAMFS_IMAGE}" ]; then
        ln -snf fit-image.its "$deploy_dir/fitImage-its-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}.its"
        if [ -n "${KERNEL_FIT_LINK_NAME}" ]; then
            ln -snf fit-image.its "$deploy_dir/fitImage-its-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_LINK_NAME}"
        fi

        if [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
            ln -snf fitImage "$deploy_dir/fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT}"
            if [ -n "${KERNEL_FIT_LINK_NAME}" ] ; then
                ln -snf fitImage "$deploy_dir/fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_LINK_NAME}"
            fi
        fi
    fi
}
addtask deploy after do_compile before do_build
