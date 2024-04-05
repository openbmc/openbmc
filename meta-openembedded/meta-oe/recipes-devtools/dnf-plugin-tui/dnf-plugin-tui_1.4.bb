SUMMARY = "A text-based user interface plugin of dnf for user to manage packages. "
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/ubinux/dnf-plugin-tui.git;branch=master;protocol=https"
SRCREV = "ad48d934c54ab01026634c90f47f151f148b8147"

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

do_install:append:class-nativesdk() {
    install -d -p ${D}/${SDKPATH}/postinst-intercepts
    cp -r ${COREBASE}/scripts/postinst-intercepts/* ${D}/${SDKPATH}/postinst-intercepts/
    sed -i -e 's/STAGING_DIR_NATIVE/NATIVE_ROOT/g' ${D}/${SDKPATH}/postinst-intercepts/*
}

FILES:${PN} += "${datadir}/dnf"
FILES:${PN} += "${SDKPATH}/postinst-intercepts"

RDEPENDS:${PN} += " \
    bash \
    dnf \
    libnewt-python \
"
BBCLASSEXTEND = "nativesdk"

SKIP_RECIPE[dnf-plugin-tui] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'does not build correctly without package_rpm in PACKAGE_CLASSES', d)}"
