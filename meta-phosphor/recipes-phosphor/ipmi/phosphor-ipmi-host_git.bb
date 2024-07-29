SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

RRECOMMENDS:${PN} += "packagegroup-obmc-ipmid-providers-libs"

inherit meson pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-systemd
inherit phosphor-ipmi-host
inherit python3native

def ipmi_whitelists(d):
    whitelists = d.getVar(
        'VIRTUAL-RUNTIME_phosphor-ipmi-providers', True) or ''
    whitelists = whitelists.split()
    whitelists = [ '{}-whitelist-native'.format(x) for x in whitelists ]
    return ' '.join(whitelists)

PACKAGECONFIG ??= "allowlist i2c-allowlist boot-flag-safe-mode softoff libuserlayer entity-manager-decorators"
PACKAGECONFIG[dynamic-sensors] = "-Ddynamic-sensors=enabled,-Ddynamic-sensors=disabled"
PACKAGECONFIG[hybrid-sensors] = "-Dhybrid-sensors=enabled,-Dhybrid-sensors=disabled"
PACKAGECONFIG[allowlist] = "-Dipmi-whitelist=enabled,-Dipmi-whitelist=disabled"
PACKAGECONFIG[i2c-allowlist] = "-Di2c-whitelist-check=enabled,-Di2c-whitelist-check=disabled"
PACKAGECONFIG[transport-oem] = "-Dtransport-oem=enabled,-Dtransport-oem=disabled"
PACKAGECONFIG[boot-flag-safe-mode] = "-Dboot-flag-safe-mode-support=enabled,-Dboot-flag-safe-mode-support=disabled"
PACKAGECONFIG[softoff] = "-Dsoftoff=enabled,-Dsoftoff=disabled"
PACKAGECONFIG[update-functional-on-fail] = "-Dupdate-functional-on-fail=enabled,-Dupdate-functional-on-fail=disabled"
PACKAGECONFIG[libuserlayer] = "-Dlibuserlayer=enabled,-Dlibuserlayer=disabled"
PACKAGECONFIG[sensors-cache] = "-Dsensors-cache=enabled,-Dsensors-cache=disabled"
PACKAGECONFIG[entity-manager-decorators] = "-Dentity-manager-decorators=enabled,-Dentity-manager-decorators=disabled"
PACKAGECONFIG[dynamic-storages-only] = "-Ddynamic-storages-only=enabled,-Ddynamic-storages-only=disabled"

DEPENDS += "nlohmann-json"
DEPENDS += "openssl"
DEPENDS += "phosphor-state-manager"
DEPENDS += "${@ipmi_whitelists(d)}"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "virtual/phosphor-ipmi-inventory-sel"
DEPENDS += "virtual/phosphor-ipmi-fru-merge-config"
DEPENDS += "virtual/phosphor-ipmi-sensor-inventory"
DEPENDS += "boost"
DEPENDS += "sdeventplus"
DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-mako-native"

VIRTUAL-RUNTIME_ipmi-config ?= "phosphor-ipmi-config"

RDEPENDS:${PN} += "clear-once"
RDEPENDS:${PN} += "phosphor-network"
RDEPENDS:${PN} += "phosphor-time-manager"
RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_ipmi-config}"
RDEPENDS:${PN} += "phosphor-watchdog"
RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_obmc-bmc-state-manager}"
RDEPENDS:${PN} += "phosphor-software-manager-version"
RDEPENDS:${PN} += "phosphor-software-manager-updater"

inherit useradd

USERADD_PACKAGES = "${PN}"
# add ipmi group
GROUPADD_PARAM:${PN} = "ipmi"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service phosphor-ipmi-host.service"

RRECOMMENDS:${PN} += "phosphor-settings-manager"


require ${BPN}.inc

# Setup IPMI Whitelist Conf files
WHITELIST_CONF = " \
        ${STAGING_DATADIR_NATIVE}/phosphor-ipmi-host/*.conf \
        ${S}/host-ipmid-whitelist.conf \
        "
EXTRA_OEMESON = " \
        -Dsensor-yaml-gen=${STAGING_DIR_NATIVE}${sensor_datadir}/sensor.yaml \
        -Dinvsensor-yaml-gen=${STAGING_DIR_NATIVE}${sensor_datadir}/invsensor.yaml \
        -Dfru-yaml-gen=${STAGING_DIR_NATIVE}${config_datadir}/fru_config.yaml \
        "
EXTRA_OEMESON:append = " \
        -Dwhitelist-conf="${WHITELIST_CONF}" \
        "

EXTRA_OEMESON:append = " -Dtests=disabled"

S = "${WORKDIR}/git"
SRC_URI += "file://merge_yamls.py "

HOSTIPMI_PROVIDER_LIBRARY += "libipmi20.so"
HOSTIPMI_PROVIDER_LIBRARY += "libsysintfcmds.so"
HOSTIPMI_PROVIDER_LIBRARY += "libusercmds.so"

NETIPMI_PROVIDER_LIBRARY += "libipmi20.so"
NETIPMI_PROVIDER_LIBRARY += "libusercmds.so"

FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${systemd_system_unitdir}/phosphor-ipmi-host.service.d/*.conf"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

# Soft Power Off
# install the soft power off service in the host shutdown target
SOFT_SVC = "xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
SOFT_TGTFMT = "obmc-host-shutdown@{0}.target"
SOFT_FMT = "../${SOFT_SVC}:${SOFT_TGTFMT}.requires/${SOFT_SVC}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'SOFT_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK[vardeps] += "OBMC_HOST_INSTANCES"

#Collect all hardcoded sensor yamls from different recipes and
#merge all of them with sensor.yaml.
python do_merge_sensors () {
    import subprocess

    # TODO: Perform the merge in a temporary directory?
    workdir = d.getVar('UNPACKDIR', True)
    nativedir = d.getVar('STAGING_DIR_NATIVE', True)
    sensorsdir = d.getVar('sensor_datadir', True)
    sensorsdir = sensorsdir[1:]
    sensorsdir = os.path.join(nativedir, sensorsdir)
    cmd = []
    cmd.append(os.path.join(workdir, 'merge_yamls.py'))
    cmd.append(os.path.join(sensorsdir, 'sensor.yaml'))

    if os.stat(os.path.join(sensorsdir, 'sensor.yaml')).st_size == 0:
        return
    fetch = bb.fetch2.Fetch([], d)
    override_urls = [url for url in fetch.urls if url.endswith('.hardcoded.yaml')]
    for url in override_urls:
        bb.debug(2, 'Overriding with source: ' + url)
        local_base = os.path.basename(fetch.localpath(url))
        filename = os.path.join(workdir, local_base)
        cmd.append(filename)

    # Invoke the script and don't catch any resulting exception.
    subprocess.check_call(cmd)
}

# python-pyyaml-native is installed by do_configure, so put this task after
addtask merge_sensors after do_configure before do_compile

IPMI_HOST_NEEDED_SERVICES = "\
    mapper-wait@-xyz-openbmc_project-control-host{}-boot.service \
    mapper-wait@-xyz-openbmc_project-control-host{}-boot-one_time.service \
    mapper-wait@-xyz-openbmc_project-control-host{}-power_restore_policy.service \
    mapper-wait@-xyz-openbmc_project-control-host{}-restriction_mode.service \
    "

do_install:append() {

    # Create service override file.
    override_dir=${D}${systemd_system_unitdir}/phosphor-ipmi-host.service.d
    override_file=${override_dir}/10-override.conf
    mkdir -p ${override_dir}
    echo "[Unit]" > ${override_file}

    # Insert host-instance based service dependencies.
    for i in ${OBMC_HOST_INSTANCES};
    do
        for s in ${IPMI_HOST_NEEDED_SERVICES};
        do
            service=$(echo ${s} | sed "s/{}/${i}/g")
            echo "Wants=${service}" >> ${override_file}
            echo "After=${service}" >> ${override_file}
        done
    done
}
