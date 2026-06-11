require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "5154a9bf84ac01dde011e367f355c07dbb329532e06810dcf3ef2af269e236e7"
