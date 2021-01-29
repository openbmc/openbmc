
# enable the overrides for the context of the conf only
OVERRIDES .= ":qemuboot-xilinx"

# Default machine targets for Xilinx QEMU (FDT Generic)
# Allow QB_MACHINE to be overridden by a BSP config
QB_MACHINE ?= "${QB_MACHINE_XILINX}"
QB_RNG=""
QB_MACHINE_XILINX_aarch64 = "-machine arm-generic-fdt"
QB_MACHINE_XILINX_arm = "-M arm-generic-fdt-7series"
QB_MACHINE_XILINX_microblaze = "-M microblaze-fdt-plnx"

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

