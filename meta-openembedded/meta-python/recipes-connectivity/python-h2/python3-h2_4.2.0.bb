DESCRIPTION = "HTTP/2 State-Machine based protocol implementation"
HOMEPAGE = "https://github.com/python-hyper/hyper-h2"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=aa3b9b4395563dd427be5f022ec321c1"

SRC_URI[sha256sum] = "c8a52129695e88b1a0578d8d2cc6842bbd79128ac685463b887ee278126ad01f"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-hpack python3-hyperframe"
