inherit allarch
inherit update-alternatives

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

RDEPENDS_${PN} += "${VIRTUAL-RUNTIME_base-utils}"

SRC_URI += "file://init"

FILES_${PN} += "${base_sbindir}/init"

do_install() {
	install -d ${D}/${base_sbindir}
	install -m 0755 ${WORKDIR}/init ${D}/${base_sbindir}/preinit-mounts
}

ALTERNATIVE_${PN} = "init"
ALTERNATIVE_TARGET[init] = "${base_sbindir}/preinit-mounts"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"

# Use a number higher than the systemd init alternative so that
# ours is enabled instead.
ALTERNATIVE_PRIORITY[init] ?= "400"
