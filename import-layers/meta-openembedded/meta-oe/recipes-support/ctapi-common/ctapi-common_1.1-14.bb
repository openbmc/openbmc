SUMMARY = "Common files and packaging infrastructure for CT-API modules"
HOMEPAGE = "http://fedoraproject.org/"
SECTION = "System Environment/Libraries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../ctapi-common.LICENSE;md5=8744cd52545ecb45befebd0da6f70f0a"

SRC_URI = "http://ftp.riken.jp/Linux/fedora/releases/23/Everything/source/SRPMS/c/${BPN}-${PV}.fc23.src.rpm;extract=ctapi-common.LICENSE \
           http://ftp.riken.jp/Linux/fedora/releases/23/Everything/source/SRPMS/c/${BPN}-${PV}.fc23.src.rpm;extract=ctapi-common.README"
SRC_URI[md5sum] = "5b7259ef1c8cd9ae801fca7a5cb548c1"
SRC_URI[sha256sum] = "87a74eb0a66055c34ba2c5c919e74f3211c5950ae1c2cbab967fdf4137f5de91"

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
