# It's useful for debugging to have post codes written to the journal
# while the machine is booting. Especially when host serial logs also get
# written. This enables the verbose output of the daemon, providing
# the logging behavior.
do_install:append:gbmc:dev() {
  sed -i '/^ExecStart=/ s,$, -v,' ${D}${systemd_system_unitdir}/lpcsnoop.service
}
