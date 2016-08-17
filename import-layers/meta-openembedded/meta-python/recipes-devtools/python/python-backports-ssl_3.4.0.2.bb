SUMMARY = "The ssl.match_hostname() function from Python 3.4"
DESCRIPTION = "The Secure Sockets layer is only actually secure if you check the hostname in the \
certificate returned by the server to which you are connecting, and verify that it matches to hostname \
that you are trying to reach. But the matching logic, defined in RFC2818, can be a bit tricky to implement \
on your own. So the ssl package in the Standard Library of Python 3.2 and greater now includes a \
match_hostname() function for performing this check instead of requiring every application to \
implement the check separately. This backport brings match_hostname() to users of earlier versions of Python"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=77b684960b86b7a4bb4a450ffde08605"

SRC_URI[md5sum] = "788214f20214c64631f0859dc79f23c6"
SRC_URI[sha256sum] = "07410e7fb09aab7bdaf5e618de66c3dac84e2e3d628352814dc4c37de321d6ae"

PYPI_PACKAGE = "backports.ssl_match_hostname"
inherit pypi setuptools
