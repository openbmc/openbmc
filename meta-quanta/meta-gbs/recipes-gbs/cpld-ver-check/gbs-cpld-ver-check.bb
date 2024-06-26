DESCRIPTION = "Report CPLD Version"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI = " file://cpld_version.sh \
            file://cpld-version.service \
          "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SYSTEMD_SERVICE:${PN} = "cpld-version.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/cpld_version.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/cpld-version.service ${D}${systemd_system_unitdir}
}
