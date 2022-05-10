LICENSE = "CLOSED"

SRC_URI = " \
    file://kmt_map.bin \
"

S = "${WORKDIR}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/kmt_map.bin ${DEPLOYDIR}/kmt_map.bin
}

addtask deploy before do_build after do_compile
