FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
