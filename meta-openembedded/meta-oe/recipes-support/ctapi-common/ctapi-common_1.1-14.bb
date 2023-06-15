SUMMARY = "Common files and packaging infrastructure for CT-API modules"
HOMEPAGE = "http://fedoraproject.org/"
SECTION = "System Environment/Libraries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../ctapi-common.LICENSE;md5=8744cd52545ecb45befebd0da6f70f0a"

SRC_URI = "https://distrib-coffee.ipsl.jussieu.fr/pub/linux/altlinux/autoimports/Sisyphus/x86_64/SRPMS.autoimports/ctapi-common-1.1-alt1_14.src.rpm;extract=ctapi-common.LICENSE \
           https://distrib-coffee.ipsl.jussieu.fr/pub/linux/altlinux/autoimports/Sisyphus/x86_64/SRPMS.autoimports/ctapi-common-1.1-alt1_14.src.rpm;extract=ctapi-common.README"
SRC_URI[sha256sum] = "0531a6db39271166f1e9060f81a0b2d47f878210dfb4f465c703fe205cc3a8ce"

do_compile() {
    install -pm 644 ${WORKDIR}/ctapi-common.LICENSE LICENSE
    install -pm 644 ${WORKDIR}/ctapi-common.README README
    echo ${libdir}/ctapi > ctapi.conf
}

do_install() {
    install -Dpm 644 ctapi.conf ${D}${sysconfdir}/ld.so.conf.d/ctapi-${TARGET_ARCH}.conf
    install -dm 755 ${D}${libdir}/ctapi
}

FILES:${PN} += "${libdir}/ctapi"
