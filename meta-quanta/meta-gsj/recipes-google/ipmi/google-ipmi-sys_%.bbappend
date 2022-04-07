SYSTEMD_SERVICE:${PN}:remove:gsj = " \
  gbmc-host-poweroff.target \
  gbmc-psu-hardreset.target \
  gbmc-psu-hardreset-pre.target \
  gbmc-psu-hardreset-time.service \
  "

do_install:append:gsj() {
   rm -r ${D}/lib
}
