FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ibm-ac-server = " \
           file://70-hwmon.rules \
           "

CHIPS:p10bmc = " \
               1e78a200.i2c/i2c-3/3-0068 \
               1e78a200.i2c/i2c-3/3-0069 \
               1e78a200.i2c/i2c-3/3-006a \
               1e78a200.i2c/i2c-3/3-006d \
               1e78a200.i2c/i2c-3/3-006b \
               1e78a200.i2c/i2c-3/3-005a \
               1e78a200.i2c/i2c-3/3-005b \
               1e78a400.i2c/i2c-7/7-0052 \
               1e78a400.i2c/i2c-7/7-0053 \
               1e78a780.i2c/i2c-14/i2c-30/30-0052 \
               "

CHIPS:huygens = " \
              i2c,162-0068 \
              i2c,164-0068 \
              i2c,262-0068 \
              i2c,264-0068 \
              i2c,362-0068 \
              i2c,364-0068 \
              i2c,462-0068 \
              i2c,464-0068 \
              i2c,562-0068 \
              i2c,564-0068 \
              i2c,662-0068 \
              i2c,664-0068 \
              i2c,762-0068 \
              i2c,764-0068 \
              i2c,862-0068 \
              i2c,864-0068 \
              "

ITEMSFMT = "ahb/apb/{0}.conf"
ITEMSFMT:p10bmc = "devices/platform/ahb/ahb--apb/ahb--apb--bus@1e78a000/{0}.conf"
ITEMSFMT:huygens = "{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

OCCS = " \
        00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
        00--00--00--0a/fsi-master/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
        "
OCCSFMT = "devices/platform/gpio-fsi/fsi-master/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:p10bmc = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:huygens = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

PACKAGECONFIG:append:p10bmc = " use-dev-path"
PACKAGECONFIG:append:huygens = " use-bus-device"

EXTRA_OEMESON:append = " -Dnegative-errno-on-fail=false -Dupdate-functional-on-fail=false"
