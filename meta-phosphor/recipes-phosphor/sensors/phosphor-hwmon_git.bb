SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"
DEPENDS += " \
        sdbusplus \
        sdeventplus \
        stdplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        gpioplus \
        cli11 \
        "
SRCREV = "fb3356aa0e41f482c05c8a98f20ba6c0d91f33d8"
PACKAGECONFIG ??= ""
PACKAGECONFIG[use-dev-path] = "-Dalways-use-devpath=enabled, -Dalways-use-devpath=disabled"
PACKAGECONFIG[use-bus-device] = "-Duse-bus-device=enabled, -Duse-bus-device=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-hwmon;branch=master;protocol=https"

SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.Hwmon@.service"

inherit pkgconfig meson
inherit obmc-phosphor-systemd

EXTRA_OEMESON:append = " -Dtests=disabled"

RDEPENDS:${PN} += "\
        bash \
        "
RRECOMMENDS:${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

FILES:${PN} += "${base_libdir}/systemd/system/xyz.openbmc_project.Hwmon@.service"

# The following postinstall script iterate over hwmon env files:
# 1. It adds HW_SENSOR_ID value if not set. The value being calculated
#    as sha256sum.
# 2. For each hwmon the script generates busconfig ACLs.
pkg_postinst:${PN}() {
    hwmon_dir="$D/etc/default/obmc/hwmon"
    dbus_dir="$D/${datadir}/dbus-1/system.d"
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
