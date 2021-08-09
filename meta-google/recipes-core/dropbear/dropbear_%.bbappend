FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"
SRC_URI:append:gbmc  = " file://dropbear.default"
SYSTEMD_AUTO_ENABLE:${PN}:prod = "disable"

FILESEXTRAPATHS:remove:gbmc:bandaid := "${THISDIR}/${PN}:"
SYSTEMD_AUTO_ENABLE:${PN}:bandaid:prod = "enable"

# Allow SSH to the gbmc-bridge node on DEV builds
do_install:append:gbmc:dev() {
  nftables_dir=${D}${sysconfdir}/nftables
  rules=$nftables_dir/50-dropbear-dev.rules
  install -d -m0755 $nftables_dir
  echo 'table inet filter {' >"$rules"
  echo '    chain gbmc_br_pub_input {' >>"$rules"
  echo '        tcp dport 22 accept' >>"$rules"
  echo '    }' >>"$rules"
  echo '}' >>"$rules"
}
