FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://power-config-host0.json"

do_install:append() {
	    install -d ${D}${datadir}/x86-power-control/
	    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}${datadir}/x86-power-control/
}

EXTRA_OECMAKE = "-DWITH_RESETBUTTON=OFF"

