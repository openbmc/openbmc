require cim-schema.inc

LICENSE = "DMTF"

RCONFLICTS:${PN} = "cim-schema-final"

SRC_URI = "http://dmtf.org/sites/default/files/cim/cim_schema_v2500/cim_schema_${PV}Experimental-MOFs.zip;subdir=${BPN}-${PV} \
    file://LICENSE \
"
SRC_URI[md5sum] = "ee4ad6441a2b65ca60a3abc53e3ec629"
SRC_URI[sha256sum] = "a44d67881325e267ef46b72eabe0c69f90470b1033b1ce7c26d9ba99072adb50"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/LICENSE;md5=eecc6f71a56ff3caf17f15bf7aeac7b4"

do_install() {
    install -d -m 0755 ${D}${datadir}/mof/cimv${PV}/
    install -d -m 0755 ${D}${datadir}/doc/cim-schema-${PV}
    install -m 644 ${UNPACKDIR}/LICENSE ${D}${datadir}/doc/cim-schema-${PV}

    cp -R --no-dereference --preserve=mode,links -v ${S}/* ${D}${datadir}/mof/cimv${PV}/
    chown -R root:root ${D}${datadir}/mof/cimv${PV}
    for i in `find ${D}${datadir}/mof/cimv${PV} -name "*.mof"`; do
        sed -i -e 's/\r//g' $i
    done
    ln -s cimv${PV} ${D}${datadir}/mof/cim-current
    ln -s cim_schema_${PV}.mof ${D}${datadir}/mof/cim-current/CIM_Schema.mof
}

FILES:${PN} = "${datadir}/mof/* ${datadir}/doc/*"
FILES:${PN}-doc = ""
