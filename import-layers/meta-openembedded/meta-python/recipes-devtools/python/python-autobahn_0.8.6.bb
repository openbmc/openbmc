DESCRIPTION = "AutobahnPython provides a WebSocket (RFC6455, Hybi-10 to -17, Hixie-76) framework for creating WebSocket-based clients and servers. \
AutobahnPython also includes an implementation of WAMP (The WebSockets Application Messaging Protocol), a light-weight,  \
asynchronous RPC/PubSub over JSON/WebSocket protocol."
HOMEPAGE = "http://autobahn.ws/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = " file://autobahn/__init__.py;beginline=1;endline=17;md5=1fdeccf6e5b70cda64cc1345597c18e3"

SRC_URI[md5sum] = "160429e7fb043940e79aa7f4ebbab57a"
SRC_URI[sha256sum] = "94ae5c6f37f9fe7c9d3f12c170fcc273afed4104a1c5508b26d6014bb9e6373e"

PYPI_PACKAGE_EXT = "zip"
inherit pypi setuptools

RDEPENDS_${PN} = "python python-pprint python-twisted-protocols python-netserver python-twisted-web"

