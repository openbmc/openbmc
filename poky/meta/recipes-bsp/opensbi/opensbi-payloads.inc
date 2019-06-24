def riscv_get_extra_oemake_image(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    if sbi_payload is None:
        return ""

    if d.getVar('RISCV_SBI_PAYLOAD'):
        return "FW_PAYLOAD_PATH=" + deploy_dir + "/" + sbi_payload

    return ""

def riscv_get_extra_oemake_fdt(d):
    sbi_fdt_payload = d.getVar('RISCV_SBI_FDT')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    if sbi_fdt_payload is None:
        return ""

    if d.getVar('RISCV_SBI_PAYLOAD'):
        # This is internal to openSBI, not a full path
        return "FW_PAYLOAD_FDT=" + sbi_fdt_payload

    return ""

def riscv_get_do_compile_depends(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD')

    if sbi_payload is None:
        return ""

    if 'linux' in sbi_payload or 'Image' in sbi_payload:
        return "virtual/kernel:do_deploy"
    if 'u-boot.bin' in sbi_payload:
        return "virtual/bootloader:do_deploy"

    return ""
