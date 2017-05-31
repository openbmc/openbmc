
RDEPENDS_${PN} += "bash"


#S = "${WORKDIR}/mlnx_cpldprog"


INSANE_SKIP_${PN}_append = "already-stripped"

inherit module

do_compile() {
    # CC is overridden in the Makefile, so override it harder in the invocation
    cd mlnx_cpldprog
    oe_runmake CC="${CC}"
}

# The "install" make target runs the binary to create links for subcommands.
# The links are excessive and this doesn't work for cross compiling.
do_install() {
	#install -d ${D}/var/mlx_svfprog
    install -d ${D}${sbindir}
    install -m 0755 ${B}/mlnx_cpldprog/mlnx_cpldprog ${D}${sbindir}
}


#FILES_${PN} += "/var/mlnx_svfprog "

FILES_${PN} += "${sbindir}"


