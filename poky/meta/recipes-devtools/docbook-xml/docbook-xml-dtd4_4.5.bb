SUMMARY = "Document type definitions for verification of XML data files"
DESCRIPTION = "Document type definitions for verification of XML data \
files against the DocBook rule set."
HOMEPAGE = "https://docbook.org"

# The upstream sources are not distributed with a license file.
# LICENSE-OASIS is included as a "patch" to workaround this. When
# upgrading this recipe, please verify whether this is still needed.

LICENSE = "OASIS"
LIC_FILES_CHKSUM = "file://${S}/LICENSE-OASIS;md5=c608985dd5f7f215e669e7639a0b1d2e"

# To support apps with xml schema backward compatibility, we must
# install a set of schemas. Install the latest based on PV and then
# name any specific versions as required. TODO: Figure out a mechanism
# to filter exactly which versions are necessary, if that's even
# possible.
#
# DocBook.org lists available release packages at https://docbook.org/xml/.
#
# The release packages relate docbook source at
# https://github.com/docbook/docbook but building them requires the
# Gradle build system. In future, it might be safer to use the source
# tree which would also enable proper SBOM generation through the
# bitbake build system.
#

SRC_URI = "https://docbook.org/xml/4.1.2/docbkx412.zip;name=payload412;subdir=docbook-4.1.2 \
           https://docbook.org/xml/4.2/docbook-xml-4.2.zip;name=payload42;subdir=docbook-4.2 \
           https://docbook.org/xml/4.3/docbook-xml-4.3.zip;name=payload43;subdir=docbook-4.3 \
           https://docbook.org/xml/4.4/docbook-xml-4.4.zip;name=payload44;subdir=docbook-4.4 \
           https://docbook.org/xml/${PV}/docbook-xml-${PV}.zip;name=payloadPV;subdir=docbook-${PV} \
           file://docbook-xml-update-catalog.xml.patch \
           file://LICENSE-OASIS"

SRC_URI[payload412.sha256sum] = "30f0644064e0ea71751438251940b1431f46acada814a062870f486c772e7772"
SRC_URI[payload42.sha256sum] = "acc4601e4f97a196076b7e64b368d9248b07c7abf26b34a02cca40eeebe60fa2"
SRC_URI[payload43.sha256sum] = "23068a94ea6fd484b004c5a73ec36a66aa47ea8f0d6b62cc1695931f5c143464"
SRC_URI[payload44.sha256sum] = "02f159eb88c4254d95e831c51c144b1863b216d909b5ff45743a1ce6f5273090"
SRC_URI[payloadPV.sha256sum] = "4e4e037a2b83c98c6c94818390d4bdd3f6e10f6ec62dd79188594e26190dc7b4"

UPSTREAM_CHECK_REGEX = "docbook-xml-(?P<pver>4(\.\d+)).zip"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure (){
    :
}

do_compile (){
    :
}

do_install () {
    install -d ${D}${sysconfdir}/xml/
    xmlcatalog --create --noout ${D}${sysconfdir}/xml/docbook-xml.xml

    for DTDVERSION in 4.1.2 4.2 4.3 4.4 4.5; do
        DEST=${datadir}/xml/docbook/schema/dtd/$DTDVERSION
        install -d -m 755 ${D}$DEST
        cp -v -R docbook-$DTDVERSION/* ${D}$DEST
        xmlcatalog --verbose --noout --add nextCatalog unused \
          file://$DEST/catalog.xml ${D}${sysconfdir}/xml/docbook-xml.xml
    done
}

# Magic environment variable is required for downstream recipe processing
XMLCATALOGS = "${sysconfdir}/xml/docbook-xml.xml"
inherit xmlcatalog

FILES:${PN} = "${datadir}/* ${sysconfdir}/xml/docbook-xml.xml"

inherit allarch
BBCLASSEXTEND = "native"
