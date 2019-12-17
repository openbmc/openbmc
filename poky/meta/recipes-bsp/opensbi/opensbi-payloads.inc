def riscv_get_extra_oemake_image(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    if sbi_payload is None:
        return ""

    return "FW_PAYLOAD_PATH=" + deploy_dir + "/" + sbi_payload

def riscv_get_extra_oemake_fdt(d):
    sbi_fdt_payload = d.getVar('RISCV_SBI_FDT')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    if sbi_fdt_payload is None:
        return ""

    return "FW_PAYLOAD_FDT_PATH=" + deploy_dir + "/" + sbi_fdt_payload

def riscv_get_do_compile_depends(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD')
    sbi_fdt_payload = d.getVar('RISCV_SBI_FDT')

    if sbi_payload is None:
        return ""

    if 'linux' in sbi_payload or 'Image' in sbi_payload:
        return "virtual/kernel:do_deploy"
    if 'u-boot.bin' in sbi_payload:
        # If we want U-Boot and a FDT then we need to depend on U-Boot and
        # the kernel
        if sbi_fdt_payload is not None:
            return "virtual/bootloader:do_deploy virtual/kernel:do_deploy"
        else:
            return "virtual/bootloader:do_deploy"
    # If we want a custom FDT then we need to depend on the kernel
    if sbi_fdt_payload is not None:
        return "virtual/kernel:do_deploy"

    return ""
