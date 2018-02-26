SUMMARY  = "Tornado is an open source version of the scalable, non-blocking web server and tools that power FriendFeed."
DESCRIPTION = "Tornado is a Python web framework and asynchronous networking library, originally developed at FriendFeed. \
By using non-blocking network I/O, Tornado can scale to tens of thousands of open connections, making it ideal for long \
polling, WebSockets, and other applications that require a long-lived connection to each user."
HOMEPAGE = "http://www.tornadoweb.org/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;startline=2;endline=15;md5=5ab7571a79ad62d0ca7aea308bac7560"

SRC_URI[md5sum] = "ab41f6765d58089f30ba51e8ec084a7b"
SRC_URI[sha256sum] = "1fb8e494cd46c674d86fac5885a3ff87b0e283937a47d74eb3c02a48c9e89ad0"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-numbers ${PYTHON_PN}-email ${PYTHON_PN}-subprocess \
                   ${PYTHON_PN}-pkgutil ${PYTHON_PN}-html ${PYTHON_PN}-json ${PYTHON_PN}-certifi ${PYTHON_PN}-threading"

RDEPENDS_${PN}-test += "${PN} ${PYTHON_PN}-unittest"

PACKAGES =+ "\
    ${PN}-test \
"

FILES_${PN}-test = " \
    ${libdir}/${PYTHON_DIR}/site-packages/*/test \
    ${libdir}/${PYTHON_DIR}/site-packages/*/testing.py* \
"
