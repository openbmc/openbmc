SUMMARY = "A port of Ruby on Rails inflector to Python"
DESCRIPTION = "Inflection is a string transformation library. It singularizes \
and pluralizes English words, and transforms strings from CamelCase to \
underscored string. Inflection is a port of Ruby on Rails’ inflector to Python."
HOMEPAGE = "http://github.com/jpvanhal/inflection"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de7833d9c2ce0052a4073928c76a13d7"

inherit allarch
inherit setuptools
inherit pypi

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-re \
        ${PYTHON_PN}-codecs \
        "
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
