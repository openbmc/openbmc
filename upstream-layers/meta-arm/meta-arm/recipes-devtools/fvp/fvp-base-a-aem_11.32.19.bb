require fvp-ecosystem.inc

LICENSE:append = " AND Artistic-2.0 AND BSL-1.0 AND BSD-2-Clause AND Unlicense"

FVP_ARCH:aarch64 = "Linux_armv8"
FVP_ARCH:x86-64 = "Linux_x86"

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Architecture/${PV_URL_SHORT}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tar.gz;subdir=${BP};name=fvp-${HOST_ARCH}"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=ac2215fd2c35830a4a52f9f910c6a552 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=da95c9d79488fe4b6115bb7f9900b505 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=4f61d2f0d834d5c29d53d828572fb6fc"

SRC_URI[fvp-aarch64.sha256sum] = "c01a1450ee2adee499183026b01f4c31f760081ffdefd5c9a9788ae58d8542ce"
SRC_URI[fvp-x86_64.sha256sum] = "b6835fd07cb390c7a32f84fe1cead84bde992813b49679137b3a7a0a026e92de"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

MODEL_CODE = "FVP_Base_RevC_AEMvA"
SHELL_SCRIPT_NAME = "${MODEL_CODE}_11.32_19_${FVP_ARCH}.sh"

INSANE_SKIP:${PN} += "dev-so"

do_install() {
    mkdir --parents ${D}${FVPDIR}/models/${FVP_ARCH} ${D}${bindir}

    ${S}/${SHELL_SCRIPT_NAME} \
        --i-agree-to-the-contained-eula \
        --no-interactive \
        --allow-existing-dir \
        --skip-platform-check \
        --destination ${D}${FVPDIR}

    fvp_link_binaries
}
# The new version no longer has any files expect the README and install
# script.  So, we have to run install before checking the license files
# for them to be present.
do_populate_lic[depends] += "${PN}:do_install"

# The files are located in a different directory that expected in
# fvp-common.inc.  So, doing it uniquely in here
fvp_link_binaries() {
    DIR="${D}${FVPDIR}/bin/"

    stat $DIR/FVP_* >/dev/null 2>&1 || bbfatal Cannot find FVP binaries in $DIR

    for FVP in $DIR/FVP_*; do
        ln -rs $FVP ${D}${bindir}/$(basename $FVP)
    done
    # But not the .so files too
    rm -f ${D}${bindir}/*.so

    cp -r ${D}${FVPDIR}/license_terms ${S}
}
