
# enable the overrides for the context of the conf only
OVERRIDES .= ":qemuboot-xilinx"

# setup the target binary
QB_SYSTEM_NAME_prepend = "qemu-xilinx/"

# Default machine targets for Xilinx QEMU (FDT Generic)
QB_MACHINE_aarch64 = "-machine arm-generic-fdt"
QB_MACHINE_arm = "-machine arm-generic-fdt-7series"
QB_MACHINE_microblaze = "-machine microblaze-generic-fdt-plnx"

# defaults
QB_DEFAULT_KERNEL ?= "none"

inherit qemuboot

# rewrite the qemuboot with the custom sysroot bindir
python do_write_qemuboot_conf_append() {
    val = os.path.join(d.getVar('BASE_WORKDIR'), d.getVar('BUILD_SYS'), 'qemu-xilinx-helper-native/1.0-r1/recipe-sysroot-native/usr/bin/')
    cf.set('config_bsp', 'STAGING_BINDIR_NATIVE', '%s' % val)

    # write out the updated version from this append
    with open(qemuboot, 'w') as f:
        cf.write(f)
}

