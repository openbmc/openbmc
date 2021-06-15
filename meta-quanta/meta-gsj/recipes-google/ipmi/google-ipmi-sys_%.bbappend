NETIPMI_PROVIDER_LIBRARY_gsj += "libsyscmds.so"
SYSTEMD_SERVICE_${PN}_remove_gsj = "gbmc-psu-hardreset.target"

do_install_append_gsj() {
   rm -rf ${D}/lib
}
