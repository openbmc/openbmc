FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append:mihawk = "-Dnegative-errno-on-fail=true"

SRC_URI:append:ibm-ac-server = " \
           file://70-hwmon.rules \
           "

CHIPS:witherspoon = " \
               bus@1e78a000/i2c-bus@100/max31785@52 \
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
CHIPS:swift = " \
               bus@1e78a000/i2c-bus@100/max31785@52 \
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
CHIPS:mihawk = " \
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

CHIPS:witherspoon-tacoma = " \
               bus@1e78a000/i2c-bus@200/max31785@52 \
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

CHIPS:p10bmc = " \
               bus@1e78a000/i2c-bus@200/power-supply@68 \
               bus@1e78a000/i2c-bus@200/power-supply@69 \
               bus@1e78a000/i2c-bus@200/power-supply@6a \
               bus@1e78a000/i2c-bus@200/power-supply@6d \
               bus@1e78a000/i2c-bus@200/power-supply@6b \
               bus@1e78a000/i2c-bus@400/max31785@52 \
               bus@1e78a000/i2c-bus@780/i2c-switch@70/i2c@3/max31785@52 \
               "

ITEMSFMT = "ahb/apb/{0}.conf"
ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
ITEMS:append:mihawk = " iio-hwmon-vdd0.conf"
ITEMS:append:mihawk = " iio-hwmon-vdd1.conf"
ITEMS:append:mihawk = " iio-hwmon-vcs0.conf"
ITEMS:append:mihawk = " iio-hwmon-vcs1.conf"
ITEMS:append:mihawk = " iio-hwmon-vdn0.conf"
ITEMS:append:mihawk = " iio-hwmon-vdn1.conf"
ITEMS:append:mihawk = " iio-hwmon-vio0.conf"
ITEMS:append:mihawk = " iio-hwmon-vio1.conf"
ITEMS:append:mihawk = " iio-hwmon-vddra.conf"
ITEMS:append:mihawk = " iio-hwmon-vddrb.conf"
ITEMS:append:mihawk = " iio-hwmon-vddrc.conf"
ITEMS:append:mihawk = " iio-hwmon-vddrd.conf"
ITEMS:append:mihawk = " iio-hwmon-12v.conf"
ITEMS:append:mihawk = " iio-hwmon-5v.conf"
ITEMS:append:mihawk = " iio-hwmon-3v.conf"
ITEMS:append:mihawk = " iio-hwmon-battery.conf"

OCCS = " \
        00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
        00--00--00--0a/fsi-master/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
        "
OCCSFMT = "devices/platform/gpio-fsi/fsi-master/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:mihawk = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:mihawk = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:p10bmc = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

# Enable and install the max31785-msl package
PACKAGECONFIG:append:ibm-ac-server = " max31785-msl"
SYSTEMD_ENVIRONMENT_FILE:max31785-msl:append:ibm-ac-server = " obmc/hwmon-max31785/max31785.conf"
SYSTEMD_LINK:max31785-msl:append:ibm-ac-server = " ../phosphor-max31785-msl@.service:multi-user.target.wants/phosphor-max31785-msl@${MACHINE}.service"

