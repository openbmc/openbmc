SUMMARY = "Facebook OEM IPMI commands"
DESCRIPTION = "Facebook OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e69ba356fa59848ffd865152a3ccc13"

SRC_URI = "git://github.com/openbmc/fb-ipmi-oem"
SRCREV = "5f8e343516c0db488e977084ca7810a6c9fafae8"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd "

inherit cmake obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE="-DENABLE_TEST=0 -DYOCTO=1"
EXTRA_OECMAKE_append_yosemitev2 = " -DBIC=1"

LIBRARY_NAMES = "libzfboemcmds.so"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES_${PN}_append = " ${datadir}/lcd-debug/post_desc.json"
FILES_${PN}_append = " ${datadir}/lcd-debug/gpio_desc.json"
FILES_${PN}_append = " ${datadir}/lcd-debug/cri_sensors.json"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install_append(){
   install -d ${D}${includedir}/fb-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/fb-ipmi-oem
}
