SUMMARY = "Fast and simple WSGI-framework for small web-applications."
DESCRIPTION = "Bottle is a fast and simple micro-framework for small web \
applications. It offers request dispatching (Routes) with url parameter \
support, templates, a built-in HTTP Server and adapters for many third \
party WSGI/HTTP-server and template engines - all in a single file and \
with no dependencies other than the Python Standard Library."
HOMEPAGE = "http://bottlepy.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=94b9b2cc7e46ccea87e00af8970826b5"

inherit allarch
inherit setuptools
inherit pypi

RDEPENDS_${PN} += " \
        python-email \
        python-json \
        python-netserver \
        "

PROVIDES += "${PN}-app"
PACKAGE_BEFORE_PN += "${PN}-app"

SUMMARY_${PN}-app = "${SRCNAME} app"
RDEPENDS_${PN}-app = "${PN}"
FILES_${PN}-app = "${bindir}/bottle.py*"

BBCLASSEXTEND = "nativesdk"
