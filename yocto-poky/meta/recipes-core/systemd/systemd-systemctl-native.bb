SUMMARY = "Wrapper for enabling systemd services"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r6"

inherit native

SRC_URI = "file://systemctl"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/systemctl ${D}${bindir}
}
