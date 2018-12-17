do_configure_prepend_microblaze() {
    # hack for microblaze, which needs xilinx.ld to literally do any linking (its hard coded in its LINK_SPEC)
    export CC="${CC} -L${S}/libgloss/microblaze"
}


# We use libgloss as if it was libxil, to avoid linking issues
do_install_append_zynqmp-pmu(){
  cp ${D}/${libdir}/libgloss.a ${D}/${libdir}/libxil.a
}
