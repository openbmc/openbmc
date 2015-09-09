FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PR := "${PR}.1"

COMPATIBLE_MACHINE_qemuphosphor = "qemuphosphor"

KBRANCH_qemuphosphor  = "standard/arm-versatile-926ejs"

KERNEL_FEATURES_append_qemuphosphor += " cfg/smp.scc"

SRC_URI += "file://qemuphosphor-standard.scc \
           "
