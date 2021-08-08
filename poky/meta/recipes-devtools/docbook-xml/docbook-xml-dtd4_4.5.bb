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
"

SRC_URI[md5sum] = "487b4d44e15cffb1f4048af23f98208e"
SRC_URI[sha256sum] = "b0f8edcf697f5318e63dd98c9a931f3fee167af0805ba441db372e0f17b2a44f"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/docbook-xml/"

S = "${WORKDIR}/docbook-xml-4.5.c31424"

inherit allarch
BBCLASSEXTEND = "native"

do_configure (){
    :
}

do_compile (){
    :
}

do_install () {
    install -d ${D}${sysconfdir}/xml/
    xmlcatalog --create --noout ${D}${sysconfdir}/xml/docbook-xml.xml

    for DTDVERSION in 4.0 4.1.2 4.2 4.3 4.4 4.5; do
        DEST=${datadir}/xml/docbook/schema/dtd/$DTDVERSION
        install -d -m 755 ${D}$DEST
        cp -v -R docbook-$DTDVERSION/* ${D}$DEST
        xmlcatalog --verbose --noout --add nextCatalog unused file://$DEST/catalog.xml ${D}${sysconfdir}/xml/docbook-xml.xml
    done
}

XMLCATALOGS = "${sysconfdir}/xml/docbook-xml.xml"
inherit xmlcatalog

FILES:${PN} = "${datadir}/* ${sysconfdir}/xml/docbook-xml.xml"
