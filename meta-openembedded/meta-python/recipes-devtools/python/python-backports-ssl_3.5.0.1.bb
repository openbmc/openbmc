SUMMARY = "The ssl.match_hostname() function from Python 3.4"
DESCRIPTION = "The Secure Sockets layer is only actually secure if you check the hostname in the \
certificate returned by the server to which you are connecting, and verify that it matches to hostname \
that you are trying to reach. But the matching logic, defined in RFC2818, can be a bit tricky to implement \
on your own. So the ssl package in the Standard Library of Python 3.2 and greater now includes a \
match_hostname() function for performing this check instead of requiring every application to \
implement the check separately. This backport brings match_hostname() to users of earlier versions of Python"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=95a5ee8fd779fbeca8b4cbca64433c87"

SRC_URI[md5sum] = "c03fc5e2c7b3da46b81acf5cbacfe1e6"
SRC_URI[sha256sum] = "502ad98707319f4a51fa2ca1c677bd659008d27ded9f6380c79e8932e38dcdf2"

PYPI_PACKAGE = "backports.ssl_match_hostname"
inherit pypi setuptools

RDEPENDS_${PN} += "${PYTHON_PN}-pkgutil"
