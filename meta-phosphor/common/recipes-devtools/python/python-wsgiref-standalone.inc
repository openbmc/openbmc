SUMMARY = "WSGI (PEP 333) Reference Library"
DESCRIPTION = "This is a standalone release of the wsgiref library, \
that provides validation support for WSGI 1.0.1 (PEP 3333) for Python \
versions < 3.2, and includes the new wsgiref.util.test() utility function."
HOMEPAGE = "http://cheeseshop.python.org/pypi/wsgiref"
SECTION = "devel/python"
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=a5c8025e305fb49e6d405769358851f6"

inherit allarch
inherit setuptools

S = "${WORKDIR}/${SRCNAME}-${PV}"
PROVIDES += "${PN}-headers ${PN}-handlers ${PN}-validate ${PN}-simple_server ${PN}-util"
PACKAGES += "${PN}-headers ${PN}-handlers ${PN}-validate ${PN}-simple_server ${PN}-util"
RDEPENDS_${PN} = "python-core"
RCONFLICTS_${PN} = "python-misc"

package_dir = "${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}"

SUMMARY_${PN}-handlers = "${SRCNAME} handlers"
RDEPENDS_${PN}-handlers = "${PN}-util ${PN}-headers"
FILES_${PN}-handlers = "${package_dir}/handlers.py*"

SUMMARY_${PN}-headers = "${SRCNAME} headers"
RDEPENDS_${PN}-headers = "${PN}"
FILES_${PN}-headers = "${package_dir}/headers.py*"

SUMMARY_${PN}-simple_server = "${SRCNAME} simple_server"
RDEPENDS_${PN}-simple_server = "${PN}-handlers"
FILES_${PN}-simple_server = "${package_dir}/simple_server.py*"

SUMMARY_${PN}-util = "${SRCNAME} util"
RDEPENDS_${PN}-util = "${PN}"
FILES_${PN}-util = "${package_dir}/util.py*"

SUMMARY_${PN}-validate = "${SRCNAME} validate"
RDEPENDS_${PN}-validate = "${PN}"
FILES_${PN}-validate = "${package_dir}/validate.py*"

FILES_${PN} = " \
        ${package_dir}/__init__.py* \
        ${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}-${PV}-*.egg-info \
        "

BBCLASSEXTEND = "nativesdk"
