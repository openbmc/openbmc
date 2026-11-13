SUMMARY = "Leancrypto Linux kernel module"
DESCRIPTION = "Standalone Linux kernel module providing PQC-resistant algorithms"

require leancrypto.inc

SRC_URI += "file://0001-linux_kernel-use-target-architecture-for-generated-headers.patch"

inherit module

B = "${S}/linux_kernel"

MAKE_TARGETS = "modules"
# Also apply when a custom Kbuild.config is supplied
EXTRA_OEMAKE += "KDIR=${STAGING_KERNEL_DIR} \
                 CONFIG_LEANCRYPTO=m \
                 CONFIG_LEANCRYPTO_TEST_MODULES=n \
"

# Supply Kbuild.config to SRC_URI in a bbappend to select algorithms
# Otherwise retain upstream settings
do_configure() {
    if [ -f ${UNPACKDIR}/Kbuild.config ]; then
        install -m 0644 ${UNPACKDIR}/Kbuild.config ${B}/Kbuild.config
    fi
}
