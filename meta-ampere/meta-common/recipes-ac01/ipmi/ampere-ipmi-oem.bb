SUMMARY = "Ampere OEM IPMI commands"
DESCRIPTION = "Ampere OEM IPMI commands"

LICENSE = "Apache-2.0"
S = "${WORKDIR}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=a6a4edad4aed50f39a66d098d74b265b"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd libgpiod"

inherit cmake obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE="-DENABLE_TEST=0 -DYOCTO=1"

LIBRARY_NAMES = "libzampoemcmds.so"


SRC_URI += " \
  file://LICENSE \
  file://CMakeLists.txt \
  file://CMakeLists.txt.in \
  file://generate-whitelist.py \
  file://ipmi-whitelist.conf \
  file://cmake-format.json \
  file://src/ipmi_to_redfish_hooks.cpp \
  file://src/storagecommands.cpp \
  file://include/commandutils.hpp \
  file://include/ipmi_to_redfish_hooks.hpp \
  file://include/sdrutils.hpp \
  file://include/storagecommands.hpp \
  file://include/types.hpp \
  "

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install_append(){
   install -d ${D}${includedir}/ampere-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/ampere-ipmi-oem
}
