SUMMARY = "LDAP client API for Python, C wrapper module around OpenLDAP 2.x with an object-oriented API" 
LICENSE = "Python-style" 
LIC_FILES_CHKSUM = "file://LICENCE;md5=a41c82edffa04912007cae1d20cac555"
HOMEPAGE = "http://www.python-ldap.org/" 
DEPENDS = "python openldap" 

SRC_URI = "file://setup.cfg.patch"
SRC_URI[md5sum] = "a9f9f16338288d118a1ae6266c993247"
SRC_URI[sha256sum] = "62f75b21c5ee744408c9d8b59878328b3bdf47899d30e8abf0c09b3ffb893ed4"

PYPI_PACKAGE = "python-ldap"
inherit pypi setuptools  
