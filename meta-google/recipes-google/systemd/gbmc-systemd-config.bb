SUMMARY = "Configures systemd settings for gBMC"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

S = "${WORKDIR}"

SRC_URI_append = " \
  file://firmware-updates.target \
  file://firmware-updates-pre.target \
  "

FILES_${PN}_append = " \
  ${systemd_unitdir}/coredump.conf.d/40-gbmc-coredump.conf \
  ${systemd_unitdir}/resolved.conf.d/40-gbmc-nomdns.conf \
  "

FILES_${PN}_append_dev = " \
  ${libdir}/sysctl.d/40-gbmc-debug.conf \
  "

SYSTEMD_SERVICE_${PN}_append = " \
  firmware-updates.target \
  firmware-updates-pre.target \
  "

# Put coredumps in the journal to ensure they stay in ram
do_install() {
    install -d -m 0755 ${D}${systemd_unitdir}/coredump.conf.d
    printf "[Coredump]\nStorage=journal\n" \
        >${D}${systemd_unitdir}/coredump.conf.d/40-gbmc-coredump.conf

    install -d -m 0755 ${D}${systemd_unitdir}/resolved.conf.d
    printf "[Resolve]\nLLMNR=no\nMulticastDNS=resolve\n" \
        >${D}${systemd_unitdir}/resolved.conf.d/40-gbmc-nomdns.conf

    install -d -m 0755 ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/firmware-updates.target ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/firmware-updates-pre.target ${D}${systemd_system_unitdir}/
}

do_install_append_dev() {
    install -d -m 0755 ${D}${libdir}/sysctl.d
    printf "kernel.sysrq = 1\n" \
        >${D}${libdir}/sysctl.d/40-gbmc-debug.conf

}
