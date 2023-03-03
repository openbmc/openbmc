def riscv_get_extra_oemake_image(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD')
    if sbi_payload is None:
        return ""

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    return "FW_PAYLOAD_PATH=" + deploy_dir + "/" + sbi_payload

def riscv_get_extra_oemake_fdt(d):
    if d.getVar('RISCV_SBI_PAYLOAD') is None:
        return ""
    sbi_fdt = d.getVar('RISCV_SBI_FDT')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')

    if sbi_fdt is None:
        return ""

    return "FW_FDT_PATH=" + deploy_dir + "/" + sbi_fdt

def riscv_get_do_compile_depends(d):
    sbi_payload = d.getVar('RISCV_SBI_PAYLOAD') or ""
    if sbi_payload == "":
        return ""

    sbi_fdt = d.getVar('RISCV_SBI_FDT') or ""

    if sbi_fdt != "" and 'u-boot.bin' in sbi_payload:
        return "virtual/kernel:do_deploy virtual/bootloader:do_deploy"

    if 'linux' in sbi_payload or 'Image' in sbi_payload:
        return "virtual/kernel:do_deploy"
    if 'u-boot.bin' in sbi_payload:
        return "virtual/bootloader:do_deploy"
    if sbi_fdt != "":
        return "virtual/kernel:do_deploy"

    return ""
