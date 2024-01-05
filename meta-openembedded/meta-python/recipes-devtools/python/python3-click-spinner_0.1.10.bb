SUMMARY = "Spinner for Click"
HOMEPAGE = "https://github.com/click-contrib/click-spinner"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI += "file://0001-Update-Versioneer-to-0.22.patch"
SRC_URI[md5sum] = "ab68ed404401421819c81cc6c0677a87"
SRC_URI[sha256sum] = "87eacf9d7298973a25d7615ef57d4782aebf913a532bba4b28a37e366e975daf"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-json"
