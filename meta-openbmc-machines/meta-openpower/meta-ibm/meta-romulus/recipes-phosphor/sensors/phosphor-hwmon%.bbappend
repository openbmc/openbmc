FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Pin the revision so the patch is guaranteed to be appliable
# TODO: Remove this when the error handling in hwmon is done:
# https://gerrit.openbmc-project.xyz/#/c/8073/
SRCREV = "26d21731c292a02ec71480fade9a06658160eafb"

SRC_URI += " file://0001-sysfs-Return-ETIMEDOUT-instead-of-throw-exception.patch"

OCCS = " \
       sbefifo@2400/occ@1/occ-hwmon@1 \
       hub@3400/cfam@1,0/sbefifo@2400/occ@2/occ-hwmon@2 \
       "

OCCSFMT = "gpio-fsi/cfam@0,0/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'OCCITEMS')}"
