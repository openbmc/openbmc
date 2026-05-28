require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "58a63ba841662e5c686b57ba1fec52ddd68c0b93bd96ac3029d55728f00bf8a2"
