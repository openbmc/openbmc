SUMMARY = "Tool for creating HTML, PDF, EPUB, man pages"
DESCRIPTION = "AsciiDoc is a text document format for writing short documents, \
articles, books and UNIX man pages."

HOMEPAGE = "http://asciidoc.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://COPYRIGHT;md5=029ad5428ba5efa20176b396222d4069"

SRC_URI = "git://github.com/asciidoc/asciidoc-py3;protocol=https;branch=main \
           file://auto-catalogs.patch"
SRCREV = "618f6e6f6b558ed1e5f2588cd60a5a6b4f881ca0"
PV .= "+py3-git${SRCPV}"

DEPENDS = "libxml2-native libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

S = "${WORKDIR}/git"

# Tell xmllint where to find the DocBook XML catalogue, because right now it
# opens /etc/xml/catalog on the host. Depends on auto-catalogs.patch
export SGML_CATALOG_FILES="file://${STAGING_ETCDIR_NATIVE}/xml/catalog"

# Not using automake
inherit autotools-brokensep
CLEANBROKEN = "1"

# target and nativesdk needs python3, but for native we can use the host.
RDEPENDS_${PN} += "python3"
RDEPENDS_remove_class-native = "python3"

BBCLASSEXTEND = "native nativesdk"
