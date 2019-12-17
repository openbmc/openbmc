SUMMARY = "A text-based user interface plugin of dnf for user to manage packages. "
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/ubinux/dnf-plugin-tui.git;branch=master "
SRCREV = "31d6866d5eda02be9a6bfb1fca9e9095b12eecd1"
PV = "1.0"

SRC_URI_append_class-target = "file://oe-remote.repo.sample"

inherit distutils3-base

S = "${WORKDIR}/git"

do_install_append() {
    install -d ${D}${datadir}/dnf
    install -m 0755 ${S}/samples/* ${D}${datadir}/dnf
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins/mkimg
    install -m 0755 ${S}/dnf-plugins/mkimg/* ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins/mkimg
    for file in $(ls ${S}/dnf-plugins/ | grep -v mkimg); do
        install -m 0755 ${S}/dnf-plugins/$file ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins
    done
}

do_install_append_class-target() {
    install -d ${D}${sysconfdir}/yum.repos.d
    install -m 0644 ${WORKDIR}/oe-remote.repo.sample ${D}${sysconfdir}/yum.repos.d
}

FILES_${PN} += "${datadir}/dnf"

RDEPENDS_${PN} += " \
    bash \
    dnf \
    libnewt-python \
"

BBCLASSEXTEND = "nativesdk"
