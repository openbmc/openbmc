FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

# Enable tpm in kernel 
SRC_URI_append_x86 = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm', 'file://tpm.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.scc', '', d)} \
    "

SRC_URI_append_x86-64 = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm', 'file://tpm.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.scc', '', d)} \
    "

SRC_URI += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm_i2c', 'file://tpm_i2c.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vtpm', 'file://vtpm.scc', '', d)} \
    "
