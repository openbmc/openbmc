inherit obmc-phosphor-license

SRC_URI += "file://init"

FILES_${PN} += "/sbin/init"

do_install() {
	install -d ${D}/sbin
	install -m 0755 ${WORKDIR}/init ${D}/sbin/init
}
