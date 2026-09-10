SUMMARY = "EEMBC CoreMark-Pro CPU benchmark"
DESCRIPTION = "CoreMark-Pro is a comprehensive CPU benchmark suite from EEMBC that evaluates processor performance using integer, floating-point, and real-world workloads."
HOMEPAGE = "https://www.eembc.org/coremark-pro/"
LICENSE = "Apache-2.0 AND LicenseRef-EEMBC-AUA"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=c84d8f508b20d579641ad151a79a8bf3"

SRC_URI = "git://github.com/eembc/coremark-pro.git;branch=main;protocol=https"
SRCREV = "4832cc67b0926c7a80a4b7ce0ce00f4640ea6bec"

UPSTREAM_CHECK_COMMITS = "1"

# Apply patch to allow cross-compilation
SRC_URI += "file://0001-allow-toolchain-override.patch"

inherit pkgconfig

# Ignore buildpaths QA check - CoreMark-Pro embeds build paths in binaries
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"

do_configure[noexec] = "1"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
    oe_runmake \
    TARGET=linux64 \
    CC="${CC}" \
    AS="${AS}" \
    AR="${AR}" \
    LD="${CC}" \
    LINKER_LAST="-lc -lm -lrt -lpthread" \
    build
}

do_install() {
    install -d ${D}${bindir}
    # CoreMark-Pro generates nine workload binaries
    for binary in cjpeg-rose7-preset core linear_alg-mid-100x100-sp loops-all-mid-10k-sp nnet_test parser-125k radix2-big-64k sha-test zip-test; do
        if [ -f ${S}/builds/linux64/gcc64/bin/${binary}.exe ]; then
            install -m 0755 ${S}/builds/linux64/gcc64/bin/${binary}.exe ${D}${bindir}/${binary}
        fi
    done
}
