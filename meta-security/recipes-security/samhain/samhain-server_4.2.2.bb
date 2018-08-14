INITSCRIPT_PARAMS = "defaults 14 86"

require samhain.inc

DEPENDS = "gmp"

SRC_URI += "file://samhain-server-volatiles"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install_append() {
    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/samhain-server-volatiles \
        ${D}${sysconfdir}/default/volatiles/samhain-server

    install -m 700 samhain-install.sh init/samhain.startLinux \
        init/samhain.startLSB ${D}/var/lib/samhain
}

RDEPENDS_${PN} += "gmp bash perl"
