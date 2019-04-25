require cim-schema.inc

LICENSE = "DMTF"

RCONFLICTS_${PN} = "cim-schema-exper"

SRC_URI = "http://dmtf.org/sites/default/files/cim/cim_schema_v2400/cim_schema_${PV}Final-MOFs.zip;subdir=${BP} \
    file://LICENSE \
"
SRC_URI[md5sum] = "a9bdf17c7374e3b5b7adeaac4842c4ad"
SRC_URI[sha256sum] = "dbfa3064ea427acd71a4bebbc172ca2dc44b0b09a6d83b0945b9ffa988a9058a"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=eecc6f71a56ff3caf17f15bf7aeac7b4"

do_install() {
    install -d -m 0755 ${D}${datadir}/mof/cimv${PV}/
    install -d -m 0755 ${D}${datadir}/doc/cim-schema-${PV}
    install -m 644 ${WORKDIR}/LICENSE ${D}${datadir}/doc/cim-schema-${PV}

    cp -R --no-dereference --preserve=mode,links -v ${S}/* ${D}${datadir}/mof/cimv${PV}/
    chown -R root:root ${D}${datadir}/mof/cimv${PV}/
    for i in `find ${D}${datadir}/mof/cimv${PV} -name "*.mof"`; do
        sed -i -e 's/\r//g' $i
    done
    ln -s cimv${PV} ${D}${datadir}/mof/cim-current
    ln -s cim_schema_${PV}.mof ${D}${datadir}/mof/cim-current/CIM_Schema.mof
}

FILES_${PN} = "${datadir}/mof/* ${datadir}/doc/*"
FILES_${PN}-doc = ""
