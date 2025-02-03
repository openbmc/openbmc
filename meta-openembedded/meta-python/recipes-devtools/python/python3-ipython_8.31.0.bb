SUMMARY = "IPython: Productive Interactive Computing"
HOMEPAGE = "https://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=59b20262b8663cdd094005bddf47af5f"

SRC_URI[sha256sum] = "b6a2274606bec6166405ff05e54932ed6e5cfecaca1fc05f2cacde7bb074d70b"

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
