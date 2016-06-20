SUMMARY = "Dummy package which ensures perl is excluded from buildtools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

PR = "r2"

python() {
    # Put the package somewhere separate to ensure it's never used except
    # when we want it
    # (note that we have to do this in anonymous python here to avoid
    # allarch.bbclass disabling itself)
    d.setVar('PACKAGE_ARCH', 'buildtools-dummy-${SDKPKGSUFFIX}')
}

PERLPACKAGES = "nativesdk-perl \
                nativesdk-perl-module-file-path"

ALLOW_EMPTY_${PN} = "1"

python populate_packages_prepend() {
    d.appendVar(d.expand('RPROVIDES_${PN}'), '${PERLPACKAGES}')
    d.appendVar(d.expand('RCONFLICTS_${PN}'), '${PERLPACKAGES}')
    d.appendVar(d.expand('RREPLACES_${PN}'), '${PERLPACKAGES}')
}

