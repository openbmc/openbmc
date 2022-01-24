SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=239668a7c6066d9e0c5382e9c8c6c0e1"

SRC_URI[sha256sum] = "9d643ff0a55b762d5cdb124b8eaa99c66322e2157b69160bc32796e824360e6d"

inherit pypi setuptools3

# Remove bundled egg-info
do_compile:prepend() {
    rm -rf ${S}/idna.egg-info
}

RDEPENDS:${PN}:class-target = "\
    ${PYTHON_PN}-codecs \
"

BBCLASSEXTEND = "native nativesdk"
