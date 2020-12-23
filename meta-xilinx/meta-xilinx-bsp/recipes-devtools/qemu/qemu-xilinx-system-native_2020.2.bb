require qemu-xilinx-native.inc

EXTRA_OECONF_append = " --target-list=${@get_qemu_system_target_list(d)}"

PACKAGECONFIG ??= "fdt alsa kvm"

PACKAGECONFIG_remove = "${@'kvm' if not os.path.exists('/usr/include/linux/kvm.h') else ''}"

DEPENDS += "pixman-native qemu-xilinx-native"

do_install_append() {
    # The following is also installed by qemu-native
    rm -f ${D}${datadir}/${BPN}/trace-events-all
    rm -rf ${D}${datadir}/${BPN}/keymaps
    rm -rf ${D}${datadir}/icons
}

