SUMMARY = "Simplified object serialization in python"
DESCRIPTION = "Marshmallow is an ORM/ODM/framework-agnostic library for converting complex datatypes, such as objects, to and from native Python datatypes."
HOMEPAGE = "https://github.com/marshmallow-code/marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
                    file://LICENSE;md5=653847350fed2e0e7b02791a35b98d59 \
                    file://docs/license.rst;md5=13da439ad060419fb7cf364523017cfb"

SRC_URI[sha256sum] = "4c1daff273513dc5eb24b219a8035559dc573c8f322558ef85f5438ddd1236dd"

inherit setuptools3 pypi

PIP_INSTALL_PACKAGE = "marshmallow"

RDEPENDS:${PN} += " \
	python3-core \
	python3-datetime \
	python3-netclient \
	python3-numbers \
	python3-json \
	python3-pprint \
	python3-packaging \
"
