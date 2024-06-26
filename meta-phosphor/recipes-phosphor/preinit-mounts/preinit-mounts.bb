LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://init"

inherit allarch
inherit update-alternatives

do_install() {
        install -d ${D}/${base_sbindir}
        install -m 0755 ${UNPACKDIR}/init ${D}/${base_sbindir}/preinit-mounts
}

RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_base-utils}"

FILES:${PN} += "${base_sbindir}/init"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
# Use a number higher than the systemd init alternative so that
# ours is enabled instead.
ALTERNATIVE_PRIORITY[init] ?= "400"

ALTERNATIVE:${PN} = "init"
ALTERNATIVE_TARGET[init] = "${base_sbindir}/preinit-mounts"
