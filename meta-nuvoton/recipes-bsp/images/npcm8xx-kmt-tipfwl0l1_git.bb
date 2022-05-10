LICENSE = "CLOSED"

SRC_URI = " \
    file://Kmt_TipFwL0_TipFwL1.bin \
"

S = "${WORKDIR}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/Kmt_TipFwL0_TipFwL1.bin ${DEPLOYDIR}/Kmt_TipFwL0_TipFwL1.bin
}

addtask deploy before do_build after do_compile
