SUMMARY = "Powerful and Pythonic XML processing library combining libxml2/libxslt with the ElementTree API."
DESCRIPTION = "lxml is a Pythonic, mature binding for the libxml2 and \
libxslt libraries. It provides safe and convenient access to these \
libraries using the ElementTree API. It extends the ElementTree API \
significantly to offer support for XPath, RelaxNG, XML Schema, XSLT, \
C14N and much more."
HOMEPAGE = "http://codespeak.net/lxml"
SECTION = "devel/python"
LICENSE = "BSD & GPLv2 & MIT & PSF"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=e4c045ebad958ead4b48008f70838403 \
                    file://doc/licenses/elementtree.txt;md5=eb34d036a6e3d56314ee49a6852ac891 \
                    file://doc/licenses/BSD.txt;md5=700a1fc17f4797d4f2d34970c8ee694b \
                    file://doc/licenses/GPL.txt;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/lxml/isoschematron/resources/rng/iso-schematron.rng;beginline=2;endline=7;md5=fc85684a8dd5fa272c086bceb0d99e10 \
                    file://src/lxml/isoschematron/resources/xsl/iso-schematron-xslt1/iso_schematron_message.xsl;beginline=2;endline=24;md5=cc86b7b2bbc678e13f58ea403eb9929b \
                    file://src/lxml/isoschematron/resources/xsl/RNG2Schtrn.xsl;beginline=2;endline=7;md5=5b03236d293dc3784205542b409d2f53 \
                    "

DEPENDS += "libxml2 libxslt"

SRC_URI[md5sum] = "ce976a2d3c630d7fde86d3a4c3a1c606"
SRC_URI[sha256sum] = "c81cb40bff373ab7a7446d6bbca0190bccc5be3448b47b51d729e37799bb5692"

DISTUTILS_BUILD_ARGS += " \
                     --with-xslt-config='pkg-config libxslt' \
                     --with-xml2-config='pkg-config libxml-2.0' \
"

DISTUTILS_INSTALL_ARGS += " \
                     --with-xslt-config='pkg-config libxslt' \
                     --with-xml2-config='pkg-config libxml-2.0' \
"

inherit pypi

# {standard input}: Assembler messages:
# {standard input}:1488805: Error: branch out of range
DEBUG_OPTIMIZATION_remove_mips = " -Og"
DEBUG_OPTIMIZATION_append_mips = " -O"
BUILD_OPTIMIZATION_remove_mips = " -Og"
BUILD_OPTIMIZATION_append_mips = " -O"

DEBUG_OPTIMIZATION_remove_mipsel = " -Og"
DEBUG_OPTIMIZATION_append_mipsel = " -O"
BUILD_OPTIMIZATION_remove_mipsel = " -Og"
BUILD_OPTIMIZATION_append_mipsel = " -O"

do_configure_prepend() {
    sed -i -e 's/--version/--modversion/' ${B}/setupinfo.py
}

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "libxml2 libxslt ${PYTHON_PN}-compression"
RDEPENDS_${PN}_class-native = "libxml2-native libxslt-native"
