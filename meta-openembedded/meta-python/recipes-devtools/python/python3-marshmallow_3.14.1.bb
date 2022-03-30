SUMMARY = "Simplified object serialization in python"
DESCRIPTION = "Marshmallow is an ORM/ODM/framework-agnostic library for converting complex datatypes, such as objects, to and from native Python datatypes."
AUTHOR = "Marius Kriegerowski <mk@quakesaver.net>"
HOMEPAGE = "https://github.com/marshmallow-code/marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
                    file://LICENSE;md5=5bccd400dedfa74364481e56aacc0b4a \
                    file://docs/license.rst;md5=13da439ad060419fb7cf364523017cfb"

SRC_URI = "git://github.com/marshmallow-code/marshmallow.git;protocol=https;branch=dev"

SRCREV = "41afdefd93afd1b95f48f45fd284ff8add3e91b7"

S = "${WORKDIR}/git"

inherit setuptools3

PIP_INSTALL_PACKAGE = "marshmallow"

RDEPENDS:${PN} += "python3-core"
