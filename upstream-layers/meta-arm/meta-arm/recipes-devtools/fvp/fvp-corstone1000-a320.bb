require fvp-ecosystem.inc

MODEL = "Corstone-1000-with-Cortex-A320"
MODEL_CODE = "FVP_Corstone_1000-A320"
PV = "11.31.cs1000_a320_2"
FVP_INSTALL_DIR = "${MODEL_CODE}_${PV}"

FVP_AARCH64_SHA256SUM = "37b67836ff09089c292c1c78fa23d60f8613a95cf4a768b70f5b4f037ad476ef"
FVP_X86_64_SHA256SUM = "f84b973efa6a65c76ae7038a281b592836081fea1920eb90fa9ca983f177a1f2"

FVP_ARCH:aarch64 = "Linux_armv8"
FVP_ARCH:x86-64 = "Linux_x86"

FVP_URL_TOKEN:aarch64 = "st=1782487701~exp=2097847701~hmac=f31b5bcea56a5f3ac8cdb3d3bfe7611e5d394987752444c07c82365dc8936338"
FVP_URL_TOKEN:x86-64 = "st=1782487742~exp=2097847742~hmac=b2e12e26a2481d2c280e93c671f0b941fe9ebce5125a4c85f7d4bc7467f3e8f5"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/FVPs-Corstone-IoT/${MODEL}/${MODEL_CODE}_${PV}_${FVP_ARCH}.tar.gz?__token__=${FVP_URL_TOKEN};subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "${FVP_AARCH64_SHA256SUM}"
SRC_URI[fvp-x86_64.sha256sum] = "${FVP_X86_64_SHA256SUM}"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "\
    file://${FVP_INSTALL_DIR}/license_terms/license_agreement.txt;md5=7fde2369510c8bcafaf4cbf42f7aa23a \
    file://${FVP_INSTALL_DIR}/license_terms/third_party_licenses/third_party_licenses.txt;md5=da95c9d79488fe4b6115bb7f9900b505 \
"

do_install() {
    mkdir --parents ${D}${FVPDIR}/models/${FVP_ARCH_DIR} ${D}${bindir}

    cp --archive --no-preserve=ownership ${S}/${FVP_INSTALL_DIR} ${D}${FVPDIR}/models/${FVP_ARCH_DIR}/

    FVP_DIR="${D}${FVPDIR}/models/${FVP_ARCH_DIR}/${FVP_INSTALL_DIR}"

    stat $FVP_DIR/bin/FVP_* >/dev/null 2>&1 || bbfatal Cannot find FVP binaries in $FVP_DIR/bin

    for FVP in $FVP_DIR/bin/FVP_*; do
        ln -rs "$FVP" "${D}${bindir}/$(basename "$FVP")"
    done
}

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"

INSANE_SKIP:${PN} += "dev-so"
