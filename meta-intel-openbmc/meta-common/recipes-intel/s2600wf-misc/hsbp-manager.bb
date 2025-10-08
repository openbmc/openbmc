SUMMARY = "HSBP Manager"
DESCRIPTION = "HSBP Manager monitors HSBPs through SMBUS"
require s2600wf.inc

S = "${WORKDIR}/git/hsbp-manager"
SYSTEMD_SERVICE:${PN} = "hsbp-manager.service"

DEPENDS += "i2c-tools libgpiod"
