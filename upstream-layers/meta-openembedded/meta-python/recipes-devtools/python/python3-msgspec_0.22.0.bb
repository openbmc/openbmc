SUMMARY = "Fast serialization and validation library for JSON, MessagePack, YAML and TOML"
HOMEPAGE = "https://github.com/jcrist/msgspec"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c21402f8022478021f697044388b97c4"

SRC_URI[sha256sum] = "0a13624a4969159fe35d8c2a3d377b2b61bbd8585e327440d5e52725affcce38"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

# msgspec builds its version with setuptools-scm, which cannot read git metadata
# from an sdist.
do_compile:prepend() {
    export SETUPTOOLS_SCM_PRETEND_VERSION=${PV}
}

RDEPENDS:${PN} += " \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
