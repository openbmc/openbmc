HOMEPAGE = "https://bugsnag.com/"
SUMMARY = "Automatic error monitoring for django, flask, etc."
DESCRIPTION = "\
  The official Python notifier for `Bugsnag <https://bugsnag.com/>`_. \
  Provides support for automatically capturing and sending exceptions \
  in your Django and other Python apps to Bugsnag, to help you find \
  and solve your bugs as fast as possible. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=f6df6ab9f1b615a140ebb2a48f61bddc"

PR = "r0"
SRCNAME = "bugsnag"

SRC_URI = "https://pypi.python.org/packages/source/b/bugsnag/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "e16360d86979890892cd388635f213e7"
SRC_URI[sha256sum] = "093934b3cd1d36ba2b89cfe1673b14ba59043417fe500a02dbf6de0df43ea962"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

DEPENDS += "python-webob python-flask python-blinker"

