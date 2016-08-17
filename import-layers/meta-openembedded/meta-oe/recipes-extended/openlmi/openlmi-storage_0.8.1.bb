SUMMARY = "CIM providers for storage management"
DESCRIPTION = "\
The openlmi-storage package contains CMPI providers for management of storage \
using Common Information Managemen (CIM) protocol. \
\
The providers can be registered in any CMPI-aware CIMOM, both OpenPegasus and \
SFCB were tested."
HOMEPAGE = "http://www.openlmi.org/"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SECTION = "System/Management"
DEPENDS = "openlmi-providers pywbem cmpi-bindings"
RDEPENDS_${PN} += "bash"

SRC_URI = "http://fedorahosted.org/released/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "898cf0d8c03b8ad6b45d65f335ddee0d"
SRC_URI[sha256sum] = "4a1ba9957750f94ea58a89cea28985564f38d7cc9aa00fcae20c51e7b32bd0a8"

inherit setuptools

do_install_append() {
    install -m 755 -d ${D}${datadir}/${BPN}
    install -m 644 ${S}/mof/* ${D}${datadir}/${BPN}/

    install -m 755 -d ${D}${sysconfdir}/openlmi/storage
    install -m 644 storage.conf ${D}${sysconfdir}/openlmi/storage/storage.conf

    install -m 755 -d ${D}${libexecdir}/pegasus
    install -m 755 pycmpiLMI_Storage-cimprovagt ${D}${libexecdir}/pegasus/
}

FILES_${PN} =+ "${sysconfdir}/openlmi/storage/storage.conf ${datadir}/${BPN}/*"
