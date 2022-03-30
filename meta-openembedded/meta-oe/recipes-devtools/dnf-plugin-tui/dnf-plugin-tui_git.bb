SUMMARY = "A text-based user interface plugin of dnf for user to manage packages. "
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/ubinux/dnf-plugin-tui.git;branch=master;protocol=https"
SRCREV = "7c45fd65dcd811def66161f6d572c3930f2ba4d8"
PV = "1.3"

SRC_URI:append:class-target = " file://oe-remote.repo.sample"

inherit setuptools3-base

S = "${WORKDIR}/git"

do_install:append() {
    install -d ${D}${datadir}/dnf
    install -m 0755 ${S}/samples/* ${D}${datadir}/dnf
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins/mkimg
    install -m 0755 ${S}/dnf-plugins/mkimg/* ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins/mkimg
    for file in $(ls ${S}/dnf-plugins/ | grep -v mkimg); do
        install -m 0755 ${S}/dnf-plugins/$file ${D}${PYTHON_SITEPACKAGES_DIR}/dnf-plugins
    done
}

do_install:append:class-target() {
    install -d ${D}${sysconfdir}/yum.repos.d
    install -m 0644 ${WORKDIR}/oe-remote.repo.sample ${D}${sysconfdir}/yum.repos.d
}

FILES:${PN} += "${datadir}/dnf"

RDEPENDS:${PN} += " \
    bash \
    dnf \
    libnewt-python \
"

BBCLASSEXTEND = "nativesdk"
SKIP_RECIPE[dnf-plugin-tui] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'does not build correctly without package_rpm in PACKAGE_CLASSES', d)}"
