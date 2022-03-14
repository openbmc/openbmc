SUMMARY = "A coroutine-based Python networking library"
DESCRIPTION = "gevent is a coroutine-based Python networking library that uses greenlet to provide \
a high-level synchronous API on top of the libevent event loop."
HOMEPAGE = "http://www.gevent.org"
LICENSE = "MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4de99aac27b470c29c6c309e0c279b65"
DEPENDS += "${PYTHON_PN}-greenlet libev c-ares"

RDEPENDS:${PN} = "${PYTHON_PN}-greenlet \
		  ${PYTHON_PN}-mime \
		  ${PYTHON_PN}-pprint \
		 "

SRC_URI[sha256sum] = "f48b64578c367b91fa793bf8eaaaf4995cb93c8bc45860e473bf868070ad094e"

inherit pypi setuptools3

# Don't embed libraries, link to the system instead
export GEVENTSETUP_EMBED = "0"

# Delete the embedded copies of libraries so we can't accidentally link to them
do_configure:append() {
	rm -rf ${S}/deps
}
