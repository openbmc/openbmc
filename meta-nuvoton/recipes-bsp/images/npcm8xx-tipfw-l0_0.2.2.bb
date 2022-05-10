LICENSE = "CLOSED"

SRC_URI = " \
    file://arbel_tip_fw_l0.${PV}.bin \
"

S = "${WORKDIR}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/arbel_tip_fw_l0.${PV}.bin ${DEPLOYDIR}/arbel_tip_fw_L0.bin
}

addtask deploy before do_build after do_compile
