DESCRIPTION = "Satyr is a collection of low-level algorithms for program \
failure processing, analysis, and reporting supporting kernel space, user \
space, Python, and Java programs"

HOMEPAGE = "https://github.com/abrt/satyr"
LICENSE = "GPLv2"

inherit autotools-brokensep python3native pkgconfig

SRC_URI = "git://github.com/abrt/satyr.git;branch=master;protocol=https \
           file://0002-fix-compile-failure-against-musl-C-library.patch \
"
SRCREV = "f8a0dbfe7fcc6e44f03d66ca5c81363aea318380"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += " \
    gdb \
    gperf-native \
    json-c \
    nettle \
    glib-2.0 \
"

PACKAGES += "python3-${BPN}"
FILES:python3-${BPN} = "${PYTHON_SITEPACKAGES_DIR}/${BPN}"

PACKAGECONFIG ??= "python3 rpm"
PACKAGECONFIG[python3] = "--with-python3, --without-python3,,python3"
PACKAGECONFIG[rpm] = "--with-rpm, --without-rpm, rpm"

do_configure:prepend() {
    ${S}/gen-version
}
