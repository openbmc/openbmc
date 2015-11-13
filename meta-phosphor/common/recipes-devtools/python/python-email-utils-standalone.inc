SUMMARY = "Standalone email package"
HOMEPAGE = "http://www.python.org/sigs/email-sig"
SECTION = "devel/python"
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=a5c8025e305fb49e6d405769358851f6"

inherit allarch
inherit setuptools

S = "${WORKDIR}/${SRCNAME}-${PV}"
PROVIDES += "${PN}-misc"
PACKAGES += "${PN}-misc"
RDPENDS_${PN} = "python-core"

package_dir = "${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}"

SUMMARY_${PN}-misc = "${SRCNAME} misc"
RDEPENDS_${PN}-misc = "${PN}"
RCONFLICTS_${PN} = "python-email"
FILES_${PN}-misc = "${PYTHON_SITEPACKAGES_DIR}"

FILES_${PN} = " \
        ${package_dir}/__init__.py* \
        ${package_dir}/utils.py* \
        ${package_dir}/_parseaddr.py* \
        ${package_dir}/encoders.py* \
        "

BBCLASSEXTEND = "nativesdk"
