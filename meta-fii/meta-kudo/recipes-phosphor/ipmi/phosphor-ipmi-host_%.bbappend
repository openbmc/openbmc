FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

#Add sensorhandler.hpp and selutilty.hpp since intel-ipmi-oem requires these libs
do_install:append:kudo(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/sensorhandler.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
}
