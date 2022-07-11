LICENSE = "CLOSED"

SRC_URI = " \
    file://arbel_a35_bootblock.${PV}.bin\
"

S = "${WORKDIR}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/arbel_a35_bootblock.${PV}.bin ${DEPLOYDIR}/arbel_a35_bootblock.bin
}

addtask deploy before do_build after do_compile
