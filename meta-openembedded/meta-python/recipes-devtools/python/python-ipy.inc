SUMMARY = "IPy - class and tools for handling of IPv4 and IPv6 addresses and networks"
DESCRIPTION = "IPy is a Python module for handling IPv4 and IPv6 Addresses and Networks \
in a fashion similar to perl's Net::IP and friends. The IP class allows \
a comfortable parsing and handling for most notations in use for IPv4 \
and IPv6 Addresses and Networks."
SECTION = "devel/python"
HOMEPAGE = "https://github.com/autocracy/python-ipy"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=848d24919845901b4f48bae5f13252e6"

SRC_URI[md5sum] = "1a90c68174234672241a7e60c7ea0fb9"
SRC_URI[sha256sum] = "2f2bf658a858d43868d8a4352b3889cf78c66e2ce678b300dcf518c9149ba621"

inherit pypi

PYPI_PACKAGE = "IPy"

BBCLASSEXTEND = "native"
