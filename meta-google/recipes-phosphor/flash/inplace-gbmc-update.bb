SUMMARY = "Google BMC Inplace Update Script"
DESCRIPTION = "Google BMC Inplace Update Script"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += " \
  bash \
  gbmc-update \
  google-key \
  "

SRC_URI += " \
 file://config-bmc.json \
 file://inplace-gbmc-verify.service \
 file://inplace-gbmc-verify.sh \
 file://inplace-gbmc-version.service \
 file://inplace-gbmc-version.sh \
 file://40-inplace-gbmc-upgrade.sh \
"

SYSTEMD_SERVICE:${PN} += "inplace-gbmc-verify.service"
SYSTEMD_SERVICE:${PN} += "inplace-gbmc-version.service"

FILES:${PN} += "${datadir}/phosphor-ipmi-flash"
FILES:${PN} += "${datadir}/gbmc-br-dhcp"

do_install() {
    sed -i 's,@ALLOW_DEV@,,' ${UNPACKDIR}/inplace-gbmc-verify.sh

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/*.sh ${D}${bindir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}

    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-bmc.json ${D}${datadir}/phosphor-ipmi-flash

    install -d ${D}${datadir}/gbmc-br-dhcp
    install -m 0644 ${UNPACKDIR}/40-inplace-gbmc-upgrade.sh ${D}${datadir}/gbmc-br-dhcp/
}

do_install:prepend:dev() {
    sed -i 's,@ALLOW_DEV@,--allow-dev,' ${UNPACKDIR}/inplace-gbmc-verify.sh
}
