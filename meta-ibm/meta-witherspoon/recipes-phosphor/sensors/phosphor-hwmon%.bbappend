FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
           file://70-hwmon.rules \
           file://70-max31785-hwmon.rules \
           file://start_max31785_hwmon.sh \
           "

WSPOON_CHIPS = " \
               i2c@1e78a000/i2c-bus@100/bmp280@77 \
               i2c@1e78a000/i2c-bus@100/dps310@76 \
               i2c@1e78a000/i2c-bus@100/max31785@52_air \
               i2c@1e78a000/i2c-bus@100/max31785@52_water \
               i2c@1e78a000/i2c-bus@100/power-supply@68 \
               i2c@1e78a000/i2c-bus@100/power-supply@69 \
               i2c@1e78a000/i2c-bus@140/ir35221@70 \
               i2c@1e78a000/i2c-bus@140/ir35221@71 \
               i2c@1e78a000/i2c-bus@180/ir35221@70 \
               i2c@1e78a000/i2c-bus@180/ir35221@71 \
               i2c@1e78a000/i2c-bus@380/tmp275@4a \
               "
WSPOON_ITEMSFMT = "ahb/apb/{0}.conf"
WSPOON_ITEMS = "${@compose_list(d, 'WSPOON_ITEMSFMT', 'WSPOON_CHIPS')}"

WSPOON_OCCS = " \
              00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
              00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
              "
WSPOON_OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
WSPOON_OCCITEMS = "${@compose_list(d, 'WSPOON_OCCSFMT', 'WSPOON_OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'WSPOON_ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'WSPOON_OCCITEMS')}"

SYSTEMD_ENVIRONMENT_FILE_max31785-msl += "obmc/hwmon-max31785/wspoon.conf"
SYSTEMD_LINK_max31785-msl += "../phosphor-max31785-msl@.service:${SYSTEMD_DEFAULT_TARGET}.wants/phosphor-max31785-msl@wspoon.service"

SYSTEMD_SERVICE_${PN} += "max31785-hwmon-helper@.service"

do_install_append() {
    install -d ${D}/${base_libdir}/udev/rules.d/
    install ${WORKDIR}/70-max31785-hwmon.rules ${D}/${base_libdir}/udev/rules.d/

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/start_max31785_hwmon.sh ${D}${bindir}
}
