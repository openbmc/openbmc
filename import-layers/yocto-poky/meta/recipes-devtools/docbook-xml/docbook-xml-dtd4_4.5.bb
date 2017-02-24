SUMMARY = "Document type definitions for verification of XML data files"
DESCRIPTION = "Document type definitions for verification of XML data \
files against the DocBook rule set, it ships with the latest DocBook 4.5 \
XML DTD, as well as a selected set of legacy DTDs for use with older \
documents, including 4.0, 4.1.2, 4.2, 4.3 and 4.4"
HOMEPAGE = "http://www.docbook.org/xml/"

LICENSE = "OASIS"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE-OASIS;md5=c608985dd5f7f215e669e7639a0b1d2e"

# Note: the upstream sources are not distributed with a license file.
# LICENSE-OASIS is included as a "patch" to workaround this. When
# upgrading this recipe, please verify whether this is still needed.
SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/d/docbook-xml/docbook-xml_${PV}.orig.tar.gz \
           file://LICENSE-OASIS \
           file://docbook-xml-update-catalog.xml.patch \
           file://docbook-xml.xml \
"

SRC_URI[md5sum] = "487b4d44e15cffb1f4048af23f98208e"
SRC_URI[sha256sum] = "b0f8edcf697f5318e63dd98c9a931f3fee167af0805ba441db372e0f17b2a44f"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/docbook-xml/"

S="${WORKDIR}/docbook-xml-4.5.c31424"

inherit allarch
BBCLASSEXTEND = "native"

SSTATEPOSTINSTFUNCS_append_class-native = " docbook_xml_dtd_sstate_postinst"
SYSROOT_PREPROCESS_FUNCS_append_class-native = " docbook_xml_dtd_sysroot_preprocess"

do_configre (){
    :
}

do_compile (){
    :
}

do_install () {
    # Refer debian https://packages.debian.org/sid/all/docbook-xml/filelist
    for DTDVERSION in 4.0 4.1.2 4.2 4.3 4.4 4.5; do
        install -d -m 755 ${D}${datadir}/xml/docbook/schema/dtd/${DTDVERSION}
        cp -v -R docbook-${DTDVERSION}/* ${D}${datadir}/xml/docbook/schema/dtd/${DTDVERSION}
    done

    install -d ${D}${sysconfdir}/xml/
    install -m 755  ${WORKDIR}/docbook-xml.xml ${D}${sysconfdir}/xml/docbook-xml.xml
}

docbook_xml_dtd_sstate_postinst () {
    if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
    then
        # Ensure that the catalog file sgml-docbook.cat is properly
        # updated when the package is installed from sstate cache.
        sed -i -e "s|file://.*/usr/share/xml|file://${datadir}/xml|g" ${SYSROOT_DESTDIR}${sysconfdir}/xml/docbook-xml.xml
    fi
}

docbook_xml_dtd_sysroot_preprocess () {
    # Update the hardcode dir in docbook-xml.xml
    sed -i -e "s|file:///usr/share/xml|file://${datadir}/xml|g" ${SYSROOT_DESTDIR}${sysconfdir}/xml/docbook-xml.xml
}

FILES_${PN} = "${datadir}/* ${sysconfdir}/xml/docbook-xml.xml"
