FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_mihawk = " --enable-negative-errno-on-fail"

SRC_URI_append_ibm-ac-server = " \
           file://70-hwmon.rules \
           file://70-max31785-hwmon.rules \
           file://start_max31785_hwmon.sh \
           "

CHIPS_witherspoon = " \
               bus@1e78a000/i2c-bus@100/max31785@52_air \
               bus@1e78a000/i2c-bus@100/max31785@52_water \
               bus@1e78a000/i2c-bus@100/power-supply@68 \
               bus@1e78a000/i2c-bus@100/power-supply@69 \
               bus@1e78a000/i2c-bus@100/bmp280@77 \
               bus@1e78a000/i2c-bus@100/dps310@76 \
               bus@1e78a000/i2c-bus@140/ir35221@70 \
               bus@1e78a000/i2c-bus@140/ir35221@71 \
               bus@1e78a000/i2c-bus@180/ir35221@70 \
               bus@1e78a000/i2c-bus@180/ir35221@71 \
               bus@1e78a000/i2c-bus@380/tmp275@4a \
               "
CHIPS_swift = " \
               bus@1e78a000/i2c-bus@100/max31785@52_air \
               bus@1e78a000/i2c-bus@100/max31785@52_water \
               bus@1e78a000/i2c-bus@100/power-supply@68 \
               bus@1e78a000/i2c-bus@100/power-supply@69 \
               bus@1e78a000/i2c-bus@440/tmp275@4a \
               bus@1e78a000/i2c-bus@440/tmp275@48 \
               bus@1e78a000/i2c-bus@300/tmp275@48 \
               bus@1e78a000/i2c-bus@300/dps310@76 \
               bus@1e78a000/i2c-bus@300/si7021a20@20 \
               bus@1e78a000/i2c-bus@380/ir35219@70 \
               bus@1e78a000/i2c-bus@380/ir35221@71 \
               bus@1e78a000/i2c-bus@380/ir35221@72 \
               bus@1e78a000/i2c-bus@3c0/ir35219@70 \
               bus@1e78a000/i2c-bus@3c0/ir35221@71 \
               bus@1e78a000/i2c-bus@3c0/ir35221@72 \
               "
CHIPS_mihawk = " \
               bus@1e78a000/i2c-bus@100/power-supply@58 \
               bus@1e78a000/i2c-bus@100/power-supply@5b \
               bus@1e78a000/i2c-bus@140/ir35221@70 \
               bus@1e78a000/i2c-bus@140/ir35221@72 \
               bus@1e78a000/i2c-bus@180/ir35221@70 \
               bus@1e78a000/i2c-bus@180/ir35221@72 \
               bus@1e78a000/i2c-bus@380/pca9545riser@70/i2c@0/tmp431@4c \
               bus@1e78a000/i2c-bus@380/pca9545riser@70/i2c@1/tmp431@4c \
               bus@1e78a000/i2c-bus@380/pca9545@71/i2c@0/tmp431@4c \
               bus@1e78a000/i2c-bus@380/pca9545@71/i2c@1/tmp431@4c \
               bus@1e78a000/i2c-bus@3c0/pca9545riser@70/i2c@0/tmp431@4c \
               bus@1e78a000/i2c-bus@3c0/pca9545riser@70/i2c@1/tmp431@4c \
               bus@1e78a000/i2c-bus@3c0/pca9545@71/i2c@0/tmp431@4c \
               bus@1e78a000/i2c-bus@3c0/pca9545@71/i2c@1/tmp431@4c \
               bus@1e78a000/i2c-bus@400/tmp275@48 \
               bus@1e78a000/i2c-bus@400/tmp275@49 \
               pwm-tacho-controller@1e786000 \
               bus@1e78a000/i2c-bus@400/emc1403@4c \
               bus@1e78a000/i2c-bus@440/pca9545@70/i2c@3/tmp275@48 \
               "

CHIPS_witherspoon-tacoma = " \
               bus@1e78a000/i2c-bus@200/max31785@52_air \
               bus@1e78a000/i2c-bus@200/max31785@52_water \
               bus@1e78a000/i2c-bus@200/power-supply@68 \
               bus@1e78a000/i2c-bus@200/power-supply@69 \
               bus@1e78a000/i2c-bus@200/bmp280@77 \
               bus@1e78a000/i2c-bus@200/dps310@76 \
               bus@1e78a000/i2c-bus@280/ir35221@70 \
               bus@1e78a000/i2c-bus@280/ir35221@71 \
               bus@1e78a000/i2c-bus@300/ir35221@70 \
               bus@1e78a000/i2c-bus@300/ir35221@71 \
               bus@1e78a000/i2c-bus@500/tmp275@4a \
               "

CHIPS_rainier = " \
               bus@1e78a000/i2c-bus@400/max31785@52 \
               bus@1e78a000/i2c-bus@780/i2c-switch@70/i2c@3/max31785@52 \
               "

ITEMSFMT = "ahb/apb/{0}.conf"
ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
ITEMS_append_mihawk += " iio-hwmon-vdd0.conf"
ITEMS_append_mihawk += " iio-hwmon-vdd1.conf"
ITEMS_append_mihawk += " iio-hwmon-vcs0.conf"
ITEMS_append_mihawk += " iio-hwmon-vcs1.conf"
ITEMS_append_mihawk += " iio-hwmon-vdn0.conf"
ITEMS_append_mihawk += " iio-hwmon-vdn1.conf"
ITEMS_append_mihawk += " iio-hwmon-vio0.conf"
ITEMS_append_mihawk += " iio-hwmon-vio1.conf"
ITEMS_append_mihawk += " iio-hwmon-vddra.conf"
ITEMS_append_mihawk += " iio-hwmon-vddrb.conf"
ITEMS_append_mihawk += " iio-hwmon-vddrc.conf"
ITEMS_append_mihawk += " iio-hwmon-vddrd.conf"
ITEMS_append_mihawk += " iio-hwmon-12v.conf"
ITEMS_append_mihawk += " iio-hwmon-5v.conf"
ITEMS_append_mihawk += " iio-hwmon-3v.conf"
ITEMS_append_mihawk += " iio-hwmon-battery.conf"

OCCS = " \
        00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
        00--00--00--0a/fsi-master/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
        "
OCCSFMT = "devices/platform/gpio-fsi/fsi-master/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_ibm-ac-server = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_ibm-ac-server = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_mihawk = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_mihawk = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_rainier = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

# Enable and install the max31785-msl package
PACKAGECONFIG_append_ibm-ac-server = " max31785-msl"
SYSTEMD_ENVIRONMENT_FILE_max31785-msl_append_ibm-ac-server = " obmc/hwmon-max31785/max31785.conf"
SYSTEMD_LINK_max31785-msl_append_ibm-ac-server = " ../phosphor-max31785-msl@.service:multi-user.target.wants/phosphor-max31785-msl@${MACHINE}.service"

SYSTEMD_SERVICE_${PN}_append_ibm-ac-server = " max31785-hwmon-helper@.service"

do_install_append_ibm-ac-server() {
    install -d ${D}/${base_libdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/70-max31785-hwmon.rules ${D}/${base_libdir}/udev/rules.d/

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/start_max31785_hwmon.sh ${D}${bindir}
}
