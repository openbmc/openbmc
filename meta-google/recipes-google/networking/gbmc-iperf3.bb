PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += "iperf3"

SRC_URI += "file://iperf3.service"

SYSTEMD_SERVICE:${PN} += "iperf3.service"

do_install() {
  # Install service definitions
  install -d -m 0755 ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/iperf3.service ${D}${systemd_system_unitdir}
}

# Allow IPERF3 to run on the gbmcbr node on DEV builds
do_install:append:dev() {
  nftables_dir=${D}${sysconfdir}/nftables
  rules=$nftables_dir/50-gbmc-iperf3-dev.rules
  install -d -m0755 $nftables_dir
  echo 'table inet filter {' >"$rules"
  echo '    chain gbmc_br_pub_input {' >>"$rules"
  echo '        tcp dport 5201 accept' >>"$rules"
  echo '    }' >>"$rules"
  echo '}' >>"$rules"
}
