require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "a31589db5188d074c63f0945c3888fad104627dfcc236fb2b97f71f89da33bc4"
