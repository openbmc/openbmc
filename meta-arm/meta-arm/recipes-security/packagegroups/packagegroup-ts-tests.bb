SUMMARY = "Trusted Services test/demo linux tools"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64-secureboot"

PACKAGES = "${PN} ${PN}-psa"

RDEPENDS:${PN} = "\
    ts-demo \
    ts-service-test \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-env-test', 'ts-remote-test', '' , d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', 'ts-uefi-test', '' , d)} \
"

SUMMARY:${PN}-psa = "PSA certification tests (psa-arch-test) for TS SPs"
RDEPENDS:${PN}-psa = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', 'ts-psa-crypto-api-test', '' , d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-its', 'ts-psa-its-api-test', '' , d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-storage', 'ts-psa-ps-api-test', '' , d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', 'ts-psa-iat-api-test', '' , d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-se-proxy', \
          'ts-psa-crypto-api-test ts-psa-its-api-test ts-psa-ps-api-test ts-psa-iat-api-test', '' , d)} \
"
