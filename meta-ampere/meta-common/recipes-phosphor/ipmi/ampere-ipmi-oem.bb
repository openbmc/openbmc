SUMMARY = "Ampere OEM IPMI commands"
DESCRIPTION = "Ampere OEM IPMI commands"

LICENSE = "Apache-2.0"
S = "${WORKDIR}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd libgpiod"

inherit cmake obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE="-DENABLE_TEST=0 -DYOCTO=1"

LIBRARY_NAMES = "libzampoemcmds.so"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/ampere-ipmi-oem.git"
SRCREV = "1463f7013a17699081c1fbf506ee8d57827d1088"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install:append(){
   install -d ${D}${includedir}/ampere-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/ampere-ipmi-oem
}
