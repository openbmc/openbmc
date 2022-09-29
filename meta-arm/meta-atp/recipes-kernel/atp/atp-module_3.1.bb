require recipes-devtools/atp/atp-source_3.1.inc
inherit module

SUMMARY = "Kernel modules for interacting wih ATP Engine and devices"
SECTION = "kernel/modules"

S = "${WORKDIR}/git"
SRC_URI = "${ATP_SRC}"

ATP_MOD_DIR = "linux"

EXTRA_OEMAKE += "-C ${ATP_MOD_DIR}"

PROVIDES = "kernel-module-atp"
RPROVIDES:${PN} = "kernel-module-atp"
KERNEL_MODULE_AUTOLOAD += "atp_buffer_manager atp_device"
MODULES_MODULE_SYMVERS_LOCATION = "${ATP_MOD_DIR}"

do_install:append() {
    install -d ${D}${includedir}/linux
    install -m 644 ${ATP_MOD_DIR}/atp_buffer_manager_user.h \
                   ${ATP_MOD_DIR}/atp_device_user.h \
                   ${D}${includedir}/linux
}
