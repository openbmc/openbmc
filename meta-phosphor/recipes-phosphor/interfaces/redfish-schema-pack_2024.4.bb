LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=50ce749decd8a3ce7ef38bdde490853b"

SRC_URI = "git://github.com/DMTF/Redfish-Publications;branch=main;protocol=https"
SRCREV = "5cc369c5c0fb39698259071b7a597f7504b24e55"
PV = "2024.4"

S = "${WORKDIR}/git"

inherit allarch
BBCLASSEXTEND = "native nativesdk"

do_compile () {
    DOLLAR="$"
    cat > ${WORKDIR}/redfish-schema-pack.pc <<EOF
prefix=${prefix}
schema_dir=${DOLLAR}{prefix}/share/redfish-schema-pack

Name: redfish-schema-pack
Description: DMTF Redfish Schema Pack
Version: ${PV}
EOF
}

do_install () {
    install -d ${D}${datadir}/redfish-schema-pack/
    for f in csdl:xml openapi:yaml json-schema:json registries:json; do
        PATHSEG=$(echo $f | sed "s/:.*//")
        FILETYPE=$(echo $f | sed "s/.*://")
        install -d ${D}${datadir}/redfish-schema-pack/$PATHSEG
        cp -R --no-dereference --preserve=mode,links -v ${S}/$PATHSEG/*.$FILETYPE \
            ${D}${datadir}/redfish-schema-pack/$PATHSEG
    done

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/redfish-schema-pack.pc ${D}${libdir}/pkgconfig/
}
