SYSTEMD_SERVICE:${PN}:remove:gsj = " \
  gbmc-host-poweroff.target \
  gbmc-psu-hardreset.target \
  "

do_install:append:gsj() {
   rm -r ${D}/lib
}
