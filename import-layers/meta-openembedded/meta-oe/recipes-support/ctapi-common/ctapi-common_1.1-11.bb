SUMMARY = "Common files and packaging infrastructure for CT-API modules"
HOMEPAGE = "http://fedoraproject.org/"
SECTION = "System Environment/Libraries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../ctapi-common.LICENSE;md5=8744cd52545ecb45befebd0da6f70f0a"

SRC_URI = "http://ftp.riken.jp/Linux/fedora/releases/20/Fedora/source/SRPMS/c/${BPN}-${PV}.fc20.src.rpm;extract=ctapi-common.LICENSE \
           http://ftp.riken.jp/Linux/fedora/releases/20/Fedora/source/SRPMS/c/${BPN}-${PV}.fc20.src.rpm;extract=ctapi-common.README"
SRC_URI[md5sum] = "f02e67487c48319376800563a2659502"
SRC_URI[sha256sum] = "32399819b0a1cac1abb2b8f0f180c572c93809faad36c46825dd536e4844c7cf"

do_compile() {
    install -pm 644 ${WORKDIR}/ctapi-common.LICENSE LICENSE
    install -pm 644 ${WORKDIR}/ctapi-common.README README
    echo ${libdir}/ctapi > ctapi.conf
}

do_install() {
    install -Dpm 644 ctapi.conf ${D}${sysconfdir}/ld.so.conf.d/ctapi-${TARGET_ARCH}.conf
    install -dm 755 ${D}${libdir}/ctapi
}

FILES_${PN} += "${libdir}/ctapi"
