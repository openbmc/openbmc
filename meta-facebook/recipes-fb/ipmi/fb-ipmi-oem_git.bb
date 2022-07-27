SUMMARY = "Facebook OEM IPMI commands"
DESCRIPTION = "Facebook OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e69ba356fa59848ffd865152a3ccc13"

SRC_URI = "git://github.com/openbmc/fb-ipmi-oem;branch=master;protocol=https"
SRCREV = "d532feca9360a2f93b763a38ce41aec13d29e35b"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd "

inherit meson pkgconfig obmc-phosphor-ipmiprovider-symlink

EXTRA_OEMESON="-Dtests=disabled -Dbic=enabled -Dhost-instances='${OBMC_HOST_INSTANCES}'"
EXTRA_OEMESON:remove:yosemitev2 = "-Dbic=enabled"
EXTRA_OEMESON="-Dmachine='${MACHINE}'"

LIBRARY_NAMES = "libzfboemcmds.so"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES:${PN}:append = " ${datadir}/lcd-debug/*.json"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install:append(){
   install -d ${D}${includedir}/fb-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/fb-ipmi-oem
}
