FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"


python () {
    # OpenBMC loads in kernel features via other mechanisms so this check
    # in the kernel-yocto.bbclass is not required
    d.setVar("KERNEL_DANGLING_FEATURES_WARN_ONLY","1")
}

