COMPATIBLE_HOST_xilinx-standalone = "${HOST_SYS}"

COMPATIBLE_MACHINE_cortexa53-zynqmp = ".*"
COMPATIBLE_MACHINE_cortexr5-zynqmp = ".*"
COMPATIBLE_MACHINE_microblaze-pmu = ".*"
COMPATIBLE_MACHINE_microblaze-plm = ".*"
COMPATIBLE_MACHINE_cortexa72-versal = ".*"
COMPATIBLE_MACHINE_cortexr5-versal = ".*"
COMPATIBLE_MACHINE_cortexa9-zynq = ".*"

XSCTH_PROC_cortexa53-zynqmp ??= "psu_cortexa53_0"
XSCTH_PROC_cortexr5-zynqmp ??= "psu_cortexr5_0"
XSCTH_PROC_microblaze-pmu ??= "psu_pmu_0"
XSCTH_PROC_cortexa72-versal ??= "psv_cortexa72_0"
XSCTH_PROC_microblaze-plm ??= "psv_pmc_0"
XSCTH_PROC_cortexr5-versal ??= "psv_cortexr5_0"
XSCTH_PROC_cortexa9-zynq ??= "ps7_cortexa9_0"

# Enable @ flag on dtc which is required by libxil
DTC_FLAGS_append_xilinx-standalone = " -@"
DT_INCLUDE_append_xilinx-standalone = " ${WORKDIR}/git/device_tree/data/kernel_dtsi/${XILINX_RELEASE_VERSION}/include/"


do_install_append_zynqmp_xilinx-standalone() {
    install -d ${D}${includedir}/devicetree
    install -m 0644 ${B}/${PN}/psu_init.c ${D}/${includedir}/devicetree/psu_init.c
    install -m 0644 ${B}/${PN}/psu_init.h ${D}/${includedir}/devicetree/psu_init.h
}
