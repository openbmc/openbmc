LICENSE = "CLOSED"

SRC_URI = " \
    file://arbel_tip_fw_l1.${PV}.bin \
"

S = "${WORKDIR}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/arbel_tip_fw_l1.${PV}.bin ${DEPLOYDIR}/arbel_tip_fw_L1.bin
}

addtask deploy before do_build after do_compile
