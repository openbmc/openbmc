# Allow SSH to the mgmt node on DEV builds
do_install_append_gbmc_dev() {
  nftables_dir=${D}${sysconfdir}/nftables
  rules=$nftables_dir/50-dropbear-dev.rules
  install -d -m0755 $nftables_dir
  echo 'table inet filter {' >"$rules"
  echo '    chain mgmt_pub_input {' >>"$rules"
  echo '        tcp dport 22 accept' >>"$rules"
  echo '    }' >>"$rules"
  echo '}' >>"$rules"
}
