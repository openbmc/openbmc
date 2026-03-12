require fvp-ecosystem.inc

MODEL = "Corstone-1000-with-Cortex-A320"
MODEL_CODE = "FVP_Corstone_1000-A320"
PV = "11.30.27"

FVP_AARCH64_SHA256SUM = "a45898fead5549779153263c3544fa1032c285d532275eb678f58cae3317b01f"
FVP_X86_64_SHA256SUM = "d57b248a1c1bc5a6040605d50af94a5151adc4da26ec9acc456ec86b819ffb76"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Corstone-IoT/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "${FVP_AARCH64_SHA256SUM}"
SRC_URI[fvp-x86_64.sha256sum] = "${FVP_X86_64_SHA256SUM}"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "\
    file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=a5ce56e117d0ab63791fbb7c35ec2211 \
"

do_install:append() {
    # This FVP embeds a Python runtime, so clean up RPATHs and remove pointless static libraries
    chrpath --delete ${D}${FVPDIR}/python/lib/python*/lib-dynload/*.so
    find ${D}${FVPDIR}/python/ -name *.a -delete
}

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
