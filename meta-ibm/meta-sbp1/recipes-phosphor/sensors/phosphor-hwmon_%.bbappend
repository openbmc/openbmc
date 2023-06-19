FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
  -Dupdate-functional-on-fail=true \
  -Dnegative-errno-on-fail=false \
"

ITEMS:append = " thermal-sensor0.conf"
ITEMS:append = " thermal-sensor1.conf"
ITEMS:append = " thermal-sensor2.conf"
ITEMS:append = " thermal-sensor3.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
