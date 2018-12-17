DESCRIPTION = "Satyr is a collection of low-level algorithms for program \
failure processing, analysis, and reporting supporting kernel space, user \
space, Python, and Java programs"

HOMEPAGE = "https://github.com/abrt/satyr"
LICENSE = "GPLv2"

inherit autotools-brokensep python3native pkgconfig

SRC_URI = "git://github.com/abrt/satyr.git \
           file://0002-fix-compile-failure-against-musl-C-library.patch \
"
SRCREV = "4baa0c765071054314d1e7e78547ce6b7c133fbc"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += " \
    gdb \
"

PACKAGES += "python3-${PN}"
FILES_python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}/${BPN}"

PACKAGECONFIG ??= "python3 rpm"
PACKAGECONFIG[python2] = "--with-python2, --without-python2,,python2"
PACKAGECONFIG[python3] = "--with-python3, --without-python3,,python3"
PACKAGECONFIG[rpm] = "--with-rpm, --without-rpm, rpm"

do_configure_prepend() {
    ${S}/gen-version
}
