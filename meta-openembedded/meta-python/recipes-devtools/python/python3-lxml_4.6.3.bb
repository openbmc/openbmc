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

SRC_URI[sha256sum] = "39b78571b3b30645ac77b95f7c69d1bffc4cf8c3b157c435a34da72e78c82468"

inherit pkgconfig pypi setuptools3

# {standard input}: Assembler messages:
# {standard input}:1488805: Error: branch out of range
DEBUG_OPTIMIZATION:remove:mips = " -Og"
DEBUG_OPTIMIZATION:append:mips = " -O"
BUILD_OPTIMIZATION:remove:mips = " -Og"
BUILD_OPTIMIZATION:append:mips = " -O"

DEBUG_OPTIMIZATION:remove:mipsel = " -Og"
DEBUG_OPTIMIZATION:append:mipsel = " -O"
BUILD_OPTIMIZATION:remove:mipsel = " -Og"
BUILD_OPTIMIZATION:append:mipsel = " -O"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "libxml2 libxslt ${PYTHON_PN}-compression"

CLEANBROKEN = "1"
