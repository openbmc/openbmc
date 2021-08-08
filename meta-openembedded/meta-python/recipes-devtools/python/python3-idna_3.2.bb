SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=239668a7c6066d9e0c5382e9c8c6c0e1"

SRC_URI[sha256sum] = "467fbad99067910785144ce333826c71fb0e63a425657295239737f7ecd125f3"

inherit pypi setuptools3

# Remove bundled egg-info
do_compile:prepend() {
    rm -rf ${S}/idna.egg-info
}

RDEPENDS:${PN}:class-target = "\
    ${PYTHON_PN}-codecs \
"

BBCLASSEXTEND = "native nativesdk"
