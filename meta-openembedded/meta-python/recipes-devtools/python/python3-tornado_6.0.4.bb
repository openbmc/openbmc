SUMMARY  = "Tornado is an open source version of the scalable, non-blocking web server and tools that power FriendFeed."
DESCRIPTION = "Tornado is a Python web framework and asynchronous networking library, originally developed at FriendFeed. \
By using non-blocking network I/O, Tornado can scale to tens of thousands of open connections, making it ideal for long \
polling, WebSockets, and other applications that require a long-lived connection to each user."
HOMEPAGE = "http://www.tornadoweb.org/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d273d63619c9aeaf15cdaf76422c4f87"

SRC_URI[md5sum] = "cf39425f3d7eba9a54287f3e795a2f23"
SRC_URI[sha256sum] = "0fe2d45ba43b00a41cd73f8be321a44936dc1aba233dee979f17a042b83eb6dc"

inherit pypi setuptools3

# Requires _compression which is currently located in misc
RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-numbers ${PYTHON_PN}-email \
                   ${PYTHON_PN}-pkgutil ${PYTHON_PN}-html ${PYTHON_PN}-json ${PYTHON_PN}-certifi ${PYTHON_PN}-threading \
                   ${PYTHON_PN}-ctypes ${PYTHON_PN}-misc"

RDEPENDS_${PN}-test += "${PN} ${PYTHON_PN}-unittest"

PACKAGES =+ "\
    ${PN}-test \
"

FILES_${PN}-test = " \
    ${libdir}/${PYTHON_DIR}/site-packages/*/test \
    ${libdir}/${PYTHON_DIR}/site-packages/*/testing.py* \
"
