SUMMARY = "IPython: Productive Interactive Computing"
HOMEPAGE = "https://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=59b20262b8663cdd094005bddf47af5f"

SRC_URI[sha256sum] = "01daa83f504b693ba523b5a407246cabde4eb4513285a3c6acaff11a66735ee4"

RDEPENDS:${PN} = "\
    python3-setuptools \
    python3-jedi \
    python3-decorator \
    python3-pickleshare \
    python3-traitlets \
    python3-prompt-toolkit \
    python3-pygments \
    python3-backcall \
    python3-pydoc \
    python3-debugger \
    python3-pexpect \
    python3-unixadmin \
    python3-misc \
    python3-sqlite3 \
    python3-stack-data \
"

inherit python_setuptools_build_meta pypi

CVE_PRODUCT = "ipython"
