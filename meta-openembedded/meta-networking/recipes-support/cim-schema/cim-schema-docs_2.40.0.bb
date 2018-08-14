require cim-schema.inc

SECTION = "doc"

LICENSE = "DMTF"

SRC_URI = "http://dmtf.org/sites/default/files/cim/cim_schema_v2400/cim_schema_${PV}Final-Doc.zip \
           file://LICENSE \
          "
SRC_URI[md5sum] = "3d01940bc1085c6c42184c25fb61f739"
SRC_URI[sha256sum] = "3174cf0f8657b19d80dc59e184778d8e553da424728cb2966fe9d5428dd84267"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=eecc6f71a56ff3caf17f15bf7aeac7b4"

do_unpack() {
        unzip -q ${DL_DIR}/cim_schema_${PV}Final-Doc.zip -d ${S}
        cp -f ${FILE_DIRNAME}/files/LICENSE ${WORKDIR}/
}

do_install() {
        install -d -m 0755 ${D}${datadir}/doc/cim-schema-${PV}-docs
        cp -R --no-dereference --preserve=mode,links -v ${S}/* ${D}${datadir}/doc/cim-schema-${PV}-docs
}

FILES_${PN} = "${datadir}/doc/*"
FILES_${PN}-doc = ""
