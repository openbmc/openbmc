FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://__init__.email_min.py"
SRC_URI += "file://issue_27934_use-float-repr.patch"

do_install_append_class-target() {
	dir=${libdir}/python${PYTHON_MAJMIN}/email
        mv ${D}/$dir/__init__.py \
                ${D}/$dir/email_full.py
        install -m644 ${WORKDIR}/__init__.email_min.py \
                ${D}/$dir/__init__.py
}

require wsgiref-${PYTHON_MAJMIN}-manifest.inc

PROVIDES_prepend = "${PN}-spwd ${PN}-email-utils "
PACKAGES_prepend = "${PN}-spwd ${PN}-email-utils "

SUMMARY_${PN}-spwd = "Shadow database support"
RDEPENDS_${PN}-spwd = "${PN}-core"
FILES_${PN}-spwd= " \
        ${libdir}/python${PYTHON_MAJMIN}/lib-dynload/spwd.so \
        ${libdir}/python${PYTHON_MAJMIN}/lib-dynload/grp.so \
        "

SUMMARY_${PN}-email-utils = "Utils from the email package"
RDEPENDS_${PN}-email-utils = "${PN}-core"
FILES_${PN}-email-utils = " \
        ${libdir}/python${PYTHON_MAJMIN}/email/__init__.py* \
        ${libdir}/python${PYTHON_MAJMIN}/email/utils.py* \
        ${libdir}/python${PYTHON_MAJMIN}/email/_parseaddr.py* \
        ${libdir}/python${PYTHON_MAJMIN}/email/encoders.py* \
        "
RDEPENDS_${PN}-email += "${PN}-email-utils"
FILES_${PN}-email += "${libdir}/python${PYTHON_MAJMIN}/email/email_full.py"
