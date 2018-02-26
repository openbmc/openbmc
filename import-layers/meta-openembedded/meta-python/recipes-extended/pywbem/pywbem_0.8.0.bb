SUMMARY = "Python WBEM Client and Provider Interface"
DESCRIPTION = "\
A Python library for making CIM (Common Information Model) operations over \
HTTP using the WBEM CIM-XML protocol. It is based on the idea that a good \
WBEM client should be easy to use and not necessarily require a large amount \
of programming knowledge. It is suitable for a large range of tasks from \
simply poking around to writing web and GUI applications. \
\
WBEM, or Web Based Enterprise Management is a manageability protocol, like \
SNMP, standardised by the Distributed Management Task Force (DMTF) available \
at http://www.dmtf.org/standards/wbem. \
\
It also provides a Python provider interface, and is the fastest and easiest \
way to write providers on the planet."
HOMEPAGE = "http://pywbem.sf.net/"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://pywbem/LICENSE.txt;md5=fbc093901857fcd118f065f900982c24"
SECTION = "Development/Libraries"
DEPENDS = "python-m2crypto-native python-typing-native"
SRC_URI = "http://jaist.dl.sourceforge.net/project/${BPN}/${BPN}/${BP}/${BP}-dev.r704.zip"
SRC_URI[md5sum] = "84072451dcdd1aa9ee82363848faf7ad"
SRC_URI[sha256sum] = "898035866d3cc741bbcd62c4ac26e633ad07b7c11d89db2472b9f923f3fd3ed8"

S = "${WORKDIR}/${BP}-dev"

inherit setuptools python-dir

do_install_append() {
    mv ${D}${bindir}/wbemcli.py ${D}${bindir}/pywbemcli
    mv ${D}${bindir}/mof_compiler.py ${D}${bindir}/mofcomp

    rm ${D}${libdir}/python2.7/site-packages/${BPN}/wbemcli.py*
    rm ${D}${libdir}/python2.7/site-packages/${BPN}/mof_compiler.py*
}

BBCLASSEXTEND = "native"
