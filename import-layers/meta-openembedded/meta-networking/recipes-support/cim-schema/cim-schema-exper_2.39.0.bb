require cim-schema.inc

LICENSE = "DMTF"

RCONFLICTS_${PN} = "cim-schema-final"

SRC_URI = "http://dmtf.org/sites/default/files/cim/cim_schema_v2390/cim_schema_${PV}Experimental-MOFs.zip;subdir=${BPN}-${PV} \
           file://LICENSE \
          "
SRC_URI[md5sum] = "b82b31c609c6bcc69521829409f58ccc"
SRC_URI[sha256sum] = "ed2096ef1ea25d189a02bbc6603fed7a48297f2987f254265763a6eecb7fc870"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=eecc6f71a56ff3caf17f15bf7aeac7b4"

do_install() {
        install -d -m 0755 ${D}${datadir}/mof/cimv${PV}/
        install -d -m 0755 ${D}${datadir}/doc/cim-schema-${PV}
        install -m 644 ${WORKDIR}/LICENSE ${D}${datadir}/doc/cim-schema-${PV}

        cp -R --no-dereference --preserve=mode,links -v ${S}/* ${D}${datadir}/mof/cimv${PV}/	
        chown -R root:root ${D}${datadir}/mof/cimv${PV}
        for i in `find ${D}${datadir}/mof/cimv${PV} -name "*.mof"`; do
                sed -i -e 's/\r//g' $i
        done
        ln -s cimv${PV} ${D}${datadir}/mof/cim-current
        ln -s cim_schema_${PV}.mof ${D}${datadir}/mof/cim-current/CIM_Schema.mof
}

FILES_${PN} = "${datadir}/mof/* ${datadir}/doc/*"
FILES_${PN}-doc = ""
