do_install_append(){
  install -d ${D}${includedir}/phosphor-ipmi-host
  install -m 0644 -D ${S}/utils.hpp ${D}${includedir}/phosphor-ipmi-host
  install -m 0644 -D ${S}/types.hpp ${D}${includedir}/phosphor-ipmi-host
  install -m 0644 -D ${S}/sensorhandler.hpp ${D}${includedir}/phosphor-ipmi-host
}
