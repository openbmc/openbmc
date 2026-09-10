SUMMARY = "Check that shebang does not exceed 256 characters"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"
do_install() {
    install -d ${D}${bindir}
    echo '#!4Shfcy9Ej8gPKDGNkyhmtxrwPinmpZQ1pCS3snbdqlNx7YfmTpHkeJakCMaDnQXx4c4TmtyJpGBn5F7IO1FShYG9EwtALDOsRKEDOJKRj2L7hW92wZTzlqx4mMqREqNa7Hrwql4DYVv8vmEMhIwvtHO3UaVpgvLY9Y3HhfopUVUMZJi5Xs9KKkRisrM0HBePG67tbeWL9ZstNuPKH1ikyeNB7PprwLrsjZ6EngCrhFTfYzRSuXhdrQQBBsLZBRDsy5QE' > ${D}${bindir}/max-shebang
    chmod 755 ${D}${bindir}/max-shebang
}

BBCLASSEXTEND = "native"
