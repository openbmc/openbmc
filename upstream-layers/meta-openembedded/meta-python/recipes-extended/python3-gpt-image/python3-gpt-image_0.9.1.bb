DESCRIPTION = "GPT Image is used to Create GUID Partition Table disk images \
on local disks. Written in pure Python gpt-image allows GPT disk images to \
be built on a local filesystem and exported to a destination device."
SUMMARY = "GPT Image (pure python)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d91d608dc8f12334a889700389229b7b"

DEPENDS += "python3-pip"

PYPI_PACKAGE = "gpt_image"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "07363562ec1cf5b50319ad0389486b576ed43c4682a16a8286822af9fd1f4594"

inherit pypi python3native python_setuptools_build_meta ptest-python-pytest

do_install:append() {
	rm -fr ${D}${libdir}/python*/site-packages/gpt-image/__pycache__
}

BBCLASSEXTEND = "native nativesdk"
