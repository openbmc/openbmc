FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
  -Dupdate-functional-on-fail=true \
  -Dnegative-errno-on-fail=false \
"

ITEMS:append = " thermistor-0.conf"
ITEMS:append = " thermistor-1.conf"
ITEMS:append = " thermistor-2.conf"
ITEMS:append = " thermistor-3.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
