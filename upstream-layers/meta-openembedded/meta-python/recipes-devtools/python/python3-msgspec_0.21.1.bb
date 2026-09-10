SUMMARY = "Fast serialization and validation library for JSON, MessagePack, YAML and TOML"
HOMEPAGE = "https://github.com/jcrist/msgspec"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c21402f8022478021f697044388b97c4"

SRC_URI[sha256sum] = "2313508e394b0d208f8f56892ca9b2799e2561329de9763b19619595a6c0f72c"

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
