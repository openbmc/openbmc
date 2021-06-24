SYSTEMD_SERVICE_${PN}_remove_gsj = " \
  gbmc-host-poweroff.target \
  gbmc-psu-hardreset.target \
  "

do_install_append_gsj() {
   rm -r ${D}/lib
}
