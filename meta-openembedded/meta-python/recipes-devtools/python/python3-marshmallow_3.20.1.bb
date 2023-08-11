SUMMARY = "Simplified object serialization in python"
DESCRIPTION = "Marshmallow is an ORM/ODM/framework-agnostic library for converting complex datatypes, such as objects, to and from native Python datatypes."
HOMEPAGE = "https://github.com/marshmallow-code/marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
                    file://LICENSE;md5=5bccd400dedfa74364481e56aacc0b4a \
                    file://docs/license.rst;md5=13da439ad060419fb7cf364523017cfb"

SRC_URI = "git://github.com/marshmallow-code/marshmallow.git;protocol=https;branch=dev"

SRCREV = "dda9a8014dfe1bcb35e21307cfd5a8e19615a284"

S = "${WORKDIR}/git"

inherit setuptools3

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
