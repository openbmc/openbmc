FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Pin the revision so the patch is guaranteed to be appliable
# TODO: Remove this when the error handling in hwmon is done:
# https://gerrit.openbmc-project.xyz/#/c/8073/
SRCREV = "a7e2c1e5c1e94246ae55313fc361d30b0e1a6165"

SRC_URI += " file://0001-sysfs-Return-ETIMEDOUT-instead-of-throw-exception.patch"

CHIPS = " \
        i2c@1e78a000/i2c-bus@440/w83773g@4c \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

OCCS = " \
       00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
       00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
       "

OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'OCCITEMS')}"
