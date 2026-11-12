require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch \
            file://0001-pyproject-lower-setuptools-requirement.patch"
SRC_URI[sha256sum] = "ad03d8abc51dbff95ebf34b27123cf0c26a3b62db7ba0cd520783686362533d1"
