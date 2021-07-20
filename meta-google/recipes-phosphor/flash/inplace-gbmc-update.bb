SUMMARY = "Google BMC Inplace Update Script"
DESCRIPTION = "Google BMC Inplace Update Script"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

PROVIDES += "virtual/bmc-update"
RPROVIDES_${PN} += "virtual/bmc-update"

RDEPENDS_${PN} += "google-key"
RDEPENDS_${PN} += "bash"

SRC_URI += " \
 file://config-bmc.json \
 file://inplace-gbmc-verify.service \
 file://inplace-gbmc-verify.sh \
 file://inplace-gbmc-version.service \
 file://inplace-gbmc-version.sh \
"

SYSTEMD_SERVICE_${PN} += "inplace-gbmc-verify.service"
SYSTEMD_SERVICE_${PN} += "inplace-gbmc-version.service"

FILES_${PN} += "${datadir}/phosphor-ipmi-flash"

do_install() {
    sed -i 's,@ALLOW_DEV@,,' ${WORKDIR}/inplace-gbmc-verify.sh

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/*.sh ${D}${bindir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}

    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bmc.json ${D}${datadir}/phosphor-ipmi-flash
}

do_install_prepend_dev() {
    sed -i 's,@ALLOW_DEV@,--allow-dev,' ${WORKDIR}/inplace-gbmc-verify.sh
}
