SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit meson
inherit obmc-phosphor-systemd

PACKAGECONFIG ??= ""
# Meson configure option to enable/disable max31785-msl
PACKAGECONFIG[max31785-msl] = "-Denable-max31785-msl=true, -Denable-max31785-msl=false"

PACKAGE_BEFORE_PN = "max31785-msl"
SYSTEMD_PACKAGES = "${PN} max31785-msl"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"
SYSTEMD_SERVICE_max31785-msl = "${@bb.utils.contains('PACKAGECONFIG', 'max31785-msl', 'phosphor-max31785-msl@.service', '', d)}"

DEPENDS += " \
        sdbusplus \
        sdeventplus \
        stdplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        gpioplus \
        cli11 \
        "

EXTRA_OEMESON += "-Dtests=disabled"

FILES_${PN} += "${base_libdir}/systemd/system/xyz.openbmc_project.Hwmon@.service"
RDEPENDS_${PN} += "\
        bash \
        "

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

FILES_max31785-msl = "\
        ${base_libdir}/systemd/system/phosphor-max31785-msl@.service \
        ${bindir}/max31785-msl \
        "
RDEPENDS_max31785-msl = "${VIRTUAL-RUNTIME_base-utils} i2c-tools bash"

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"

SRCREV = "9c4053455f479c85221712f790187076669e0d59"

S = "${WORKDIR}/git"

# The following postinstall script iterate over hwmon env files:
# 1. It adds HW_SENSOR_ID value if not set. The value being calculated
#    as sha256sum.
# 2. For each hwmon the script generates busconfig ACLs.
pkg_postinst_${PN}() {
    hwmon_dir="$D/etc/default/obmc/hwmon"
    dbus_dir="$D/etc/dbus-1/system.d"

    if [ -n "$D" -a -d "${hwmon_dir}" ]; then
        # Remove existing links and replace with actual copy of the file to prevent
        # HW_SENSOR_ID variable override for different sensors' instances.
        find "${hwmon_dir}" -type l -name \*.conf | while read f; do
            path="$(readlink -f $f)"
            rm -f "${f}"
            cp "${path}" "${f}"
        done

        find "${hwmon_dir}" -type f -name \*.conf | while read f; do
            path="/${f##${hwmon_dir}/}"
            path="${path%.conf}"
            sensor_id="$(printf "%s" "${path}" | sha256sum | cut -d\  -f1)"
            acl_file="${dbus_dir}/xyz.openbmc_project.Hwmon-${sensor_id}.conf"

            egrep -q '^HW_SENSOR_ID\s*=' "${f}" ||
                printf "\n# Sensor id for %s\nHW_SENSOR_ID = \"%s\"\n" "${path}" "${sensor_id}" >> "${f}"

            # Extract HW_SENSOR_ID that could be either quoted or unquoted string.
            sensor_id="$(sed -n 's,^HW_SENSOR_ID\s*=\s*"\?\(.[^" ]\+\)\s*"\?,\1,p' "${f}")"

            [ ! -f "${acl_file}" ] || continue
            path_s="$(echo "${path}" | sed 's,\-\-,\\-\\-,g')"

            cat <<EOF>"${acl_file}"
<!DOCTYPE busconfig PUBLIC "-//freedesktop//DTD D-BUS Bus Configuration 1.0//EN"
 "http://www.freedesktop.org/standards/dbus/1.0/busconfig.dtd">
<busconfig>
  <policy user="root">
    <!-- ${path_s} -->
    <allow own="xyz.openbmc_project.Hwmon-${sensor_id}.Hwmon1"/>
    <allow send_destination="xyz.openbmc_project.Hwmon-${sensor_id}.Hwmon1"/>
  </policy>
</busconfig>
EOF
        done
    fi
}
