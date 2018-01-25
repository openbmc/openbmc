FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

NAMES = " apb/i2c@1e78a000/i2c-bus@1c0/tmp421@1d \
         apb/i2c@1e78a000/i2c-bus@1c0/tmp421@1f \
         apb/i2c@1e78a000/i2c-bus@1c0/tmp421@4d \
         apb/i2c@1e78a000/i2c-bus@1c0/tmp421@4f \
         apb/i2c@1e78a000/i2c-bus@1c0/nvt210@4c \
         apb/i2c@1e78a000/i2c-bus@180/ir38163@42 \
         apb/i2c@1e78a000/i2c-bus@180/ir38163@44 \
         apb/i2c@1e78a000/i2c-bus@180/ir38163@46 \
         apb/i2c@1e78a000/i2c-bus@180/ir38163@48 \
         apb/i2c@1e78a000/i2c-bus@180/pxm1310@02 \
         apb/i2c@1e78a000/i2c-bus@180/pxm1310@04 \
         apb/i2c@1e78a000/i2c-bus@80/tmp421@1c \
         apb/i2c@1e78a000/i2c-bus@80/tmp421@1e \
         apb/i2c@1e78a000/i2c-bus@80/tmp421@2a \
         apb/i2c@1e78a000/i2c-bus@80/tmp421@4e \
         apb/i2c@1e78a000/i2c-bus@300/adm1278@10 \
         apb/i2c@1e78a000/i2c-bus@300/adm1278@11 \
         apb/i2c@1e78a000/i2c-bus@340/pca9641@70/i2c-arb/adm1278@12 \
         apb/i2c@1e78a000/i2c-bus@340/pca9641@70/i2c-arb/max31790@20 \
         apb/i2c@1e78a000/i2c-bus@340/pca9641@70/i2c-arb/max31790@23 \
         apb/i2c@1e78a000/i2c-bus@340/pca9641@70/i2c-arb/tmp421@1d \
         apb/i2c@1e78a000/i2c-bus@340/pca9641@70/i2c-arb/ds1100@58 \
         flash-controller@1e631000/spi2@0 \
"

ITEMSFMT = "ahb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"
ITEMS += "iio-hwmon.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
