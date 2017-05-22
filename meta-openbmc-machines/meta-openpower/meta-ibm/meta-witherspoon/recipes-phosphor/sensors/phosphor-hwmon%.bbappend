FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

I2CCHIPS = "i2c@1e78a000/i2c-bus@100/bmp280@77"
I2CITEMSFMT = "ahb/apb/{0}.conf"

I2CITEMS = "${@compose_list(d, 'I2CITEMSFMT', 'I2CCHIPS')}"

ENVS = "obmc/hwmon/{0}"
I2CSYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'I2CITEMS')}"
