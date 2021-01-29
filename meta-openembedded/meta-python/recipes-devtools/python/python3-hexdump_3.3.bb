SUMMARY = "view/edit your binary with any text editor"

# The homepage listed on pypi is [1] but the repository has been removed.
# Instead, set the homepage to pypi.
#
# [1] https://bitbucket.org/techtonik/hexdump/
HOMEPAGE = "https://pypi.org/project/hexdump/"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/PD;md5=b3597d12946881e13cb3b548d1173851"

PYPI_PACKAGE_EXT = "zip"

inherit pypi distutils3

SRC_URI[sha256sum] = "d781a43b0c16ace3f9366aade73e8ad3a7bd5137d58f0b45ab2d3f54876f20db"

S = "${WORKDIR}"

BBCLASSEXTEND = "native nativesdk"

do_cleanup_hexfile() {
    rm ${D}${datadir}/data/hexfile.bin
    rmdir ${D}${datadir}/data ${D}${datadir}
}

addtask cleanup_hexfile before do_package after do_install
