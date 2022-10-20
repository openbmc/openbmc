# Each platform will need a service file that starts
# at an appropriate time per system.  For instance, if
# your system relies on passive dbus for fans or other
# sensors then it may be prudent to wait for all of them.
SUMMARY = "Phosphor PID Fan Control"
DESCRIPTION = "Fan Control"
HOMEPAGE = "github.com/openbmc/phosphor-pid-control"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "libevdev"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"
DEPENDS += "boost"
# We depend on this to be built first so we can build our providers.
DEPENDS += "phosphor-ipmi-host"
SRCREV = "cf8ee3006c526a8ceeeacaeebe8f203a0082d0cf"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-pid-control;branch=master;protocol=https"

S = "${WORKDIR}/git"
SERVICE_FILE = "phosphor-pid-control.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${SERVICE_FILE}"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink
inherit systemd

EXTRA_OECONF = " \
  SYSTEMD_TARGET="multi-user.target" \
  --disable-tests \
       "

FILES:${PN} = "${bindir}/swampd ${bindir}/setsensor"
# The following installs the OEM IPMI handler for the fan controls.
FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

HOSTIPMI_PROVIDER_LIBRARY += "libmanualcmds.so"
config_datadir = "${datadir}/swampd/"
# config_path is the location swampd expects to find a json configuration.
# the file is expected to be named config.json
config_path = "${config_datadir}config.json"
