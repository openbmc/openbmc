NAME = "wsgiref"

PROVIDES_prepend = "${PN}-${NAME} ${PN}-${NAME}-headers ${PN}-${NAME}-handlers ${PN}-${NAME}-validate ${PN}-${NAME}-simple_server ${PN}-${NAME}-util "
PACKAGES_prepend = "${PN}-${NAME} ${PN}-${NAME}-headers ${PN}-${NAME}-handlers ${PN}-${NAME}-validate ${PN}-${NAME}-simple_server ${PN}-${NAME}-util "

package_dir = "${libdir}/python${PYTHON_MAJMIN}/${NAME}"

SUMMARY_${PN}-${NAME}-handlers = "${NAME} handlers"
RDEPENDS_${PN}-${NAME}-handlers = "${PN}-${NAME}-util ${PN}-${NAME}-headers"
FILES_${PN}-${NAME}-handlers = "${package_dir}/handlers.py*"

SUMMARY_${PN}-${NAME}-headers = "${NAME} headers"
RDEPENDS_${PN}-${NAME}-headers = "${PN}-${NAME}"
FILES_${PN}-${NAME}-headers = "${package_dir}/headers.py*"

SUMMARY_${PN}-${NAME}-simple_server = "${NAME} simple server"
RDEPENDS_${PN}-${NAME}-simple_server = "${PN}-${NAME}-handlers"
FILES_${PN}-${NAME}-simple_server = "${package_dir}/simple_server.py*"

SUMMARY_${PN}-${NAME}-util = "${NAME} util"
RDEPENDS_${PN}-${NAME}-util = "${PN}-${NAME}"
FILES_${PN}-${NAME}-util = "${package_dir}/util.py*"

SUMMARY_${PN}-${NAME}-validate = "${NAME} validate"
RDEPENDS_${PN}-${NAME}-validate = "${PN}-${NAME}"
FILES_${PN}-${NAME}-validate = "${package_dir}/validate.py*"

SUMMARY_${PN}-${NAME} = "WSGI (PEP 333) Reference Library"
RDEPENDS_${PN}-${NAME} = "${PN}-core"
FILES_${PN}-${NAME} = " \
        ${package_dir}/__init__.py* \
        ${package_dir}.egg-info \
        "
