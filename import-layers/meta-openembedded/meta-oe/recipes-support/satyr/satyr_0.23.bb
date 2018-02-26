DESCRIPTION = "Satyr is a collection of low-level algorithms for program \
failure processing, analysis, and reporting supporting kernel space, user \
space, Python, and Java programs"

HOMEPAGE = "https://github.com/abrt/satyr"
LICENSE = "GPLv2"

inherit autotools-brokensep python3native pkgconfig

SRC_URI = "git://github.com/abrt/satyr.git \
           file://0001-do-not-support-python2.patch \
           file://0002-fix-compile-failure-against-musl-C-library.patch \
"
SRCREV = "4742fd4857c446b96f712d52e2e4cd0b6cfe7398"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += " \
    gdb \
"

PACKAGES += "python3-${PN}"
FILES_python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}/${BPN}"

PACKAGECONFIG ??= "python3 rpm"
PACKAGECONFIG[python3] = "--with-python3, --without-python3,,python3"
PACKAGECONFIG[pythonmanpage] = "--enable-python-manpage, --disable-python-manpage"
PACKAGECONFIG[rpm] = "--with-rpm, --without-rpm, rpm"

do_configure_prepend() {
    ${S}/gen-version
}
