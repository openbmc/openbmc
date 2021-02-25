FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://phosphor-vlan.scc"
SRC_URI += "file://phosphor-vlan.cfg"

KERNEL_FEATURES_append = " phosphor-vlan"
KERNEL_FEATURES_remove_qemuall = " phosphor-vlan"

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
