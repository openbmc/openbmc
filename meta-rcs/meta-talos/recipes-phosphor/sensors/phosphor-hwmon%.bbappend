FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

TALOS_CHIPS = " \
       i2c@1e78a000/i2c-bus@440/max31785@52 \
       i2c@1e78a000/i2c-bus@440/w83773g@4c \
       i2c@1e78a000/i2c-bus@440/power-supply@68 \
       i2c@1e78a000/i2c-bus@440/power-supply@69 \
       "
TALOS_ITEMSFMT = "ahb/apb/{0}.conf"
TALOS_ITEMS = "${@compose_list(d, 'TALOS_ITEMSFMT', 'TALOS_CHIPS')}"

TALOS_OCCS = " \
       00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
       00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
       "

TALOS_OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
TALOS_OCCITEMS = "${@compose_list(d, 'TALOS_OCCSFMT', 'TALOS_OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'TALOS_ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'TALOS_OCCITEMS')}"

SYSTEMD_ENVIRONMENT_FILE_max31785-msl += "obmc/hwmon-max31785/talos.conf"
SYSTEMD_LINK_max31785-msl += "../phosphor-max31785-msl@.service:${SYSTEMD_DEFAULT_TARGET}.wants/phosphor-max31785-msl@talos.service"
